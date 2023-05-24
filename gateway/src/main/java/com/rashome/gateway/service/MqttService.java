package com.rashome.gateway.service;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rashome.gateway.commons.exception.IotGatewayException;
import com.rashome.gateway.commons.util.DateUtil;
import com.rashome.gateway.commons.util.JsonUtil;
import com.rashome.gateway.dto.DeviceDataVO;
import com.rashome.gateway.dto.DeviceVO;
import com.rashome.gateway.dto.IotDeviceDataVO;

@Service
public class MqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    private IMqttClient mqttClient;

    @Autowired
    private CacheAndMetricsService cacheAndMetricsService;

    @Autowired
    private ReportService reportService;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS");


    /**
     * 初始化 Mqtt Client, 尝试连接 Mqtt Broker
     * @param mqttBrokerUrl
     */
    public MqttService(@Value("${mqtt.broker.url}") String mqttBrokerUrl) {

        // 连接 Mqtt Broker
        try {
            mqttClient = new MqttClient(mqttBrokerUrl, UUID.randomUUID().toString(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true); // 自动重连
            options.setCleanSession(false); // 干净 Session
            options.setConnectionTimeout(10); // 连接超时时间
            mqttClient.connect(options);
        } catch (MqttException e) {
            logger.error("Mqtt 连接失败", new IotGatewayException(e));
        }

        // 连接 topic
        try {
            mqttClient.subscribe("board/+/data", this::messageHandler);
            mqttClient.subscribe("board/+/sensor/+/data", this::messageHandler);
        } catch (MqttException e) {
            logger.error("Mqtt 订阅失败", new IotGatewayException(e));
        }
    }

    /**
     * 消息处理方法
     * @param topic
     * @param message
     */
    private void messageHandler(String topic, MqttMessage message) {

        // 判断数据是否合法
        IotDeviceDataVO iotDeviceDataVO;
        try {
            iotDeviceDataVO = checkMessageFormat(message);
        } catch (IotGatewayException e) {
            logger.warn("Iot 设备消息处理失败", e);
            return;
        }


        // 判断是 传感器数据 还是 控制板数据
        boolean isSensorIotDeviceData = true;
        if (iotDeviceDataVO.getParentUuid().equals(iotDeviceDataVO.getDeviceUuid())) {
            isSensorIotDeviceData = false;
        }

        // 判断设备是否已注册. 设备 包含传感器和控制板
        Long deviceId;
        try {
            deviceId = checkIfRegist(iotDeviceDataVO);
        } catch (RestClientException | JsonProcessingException | IllegalArgumentException | IotGatewayException e) {
            if (isSensorIotDeviceData) {
                logger.warn("注册传感器失败", e);
            } else {
                logger.warn("注册控制板失败", e);
            }
            return;
        }

        // 发送数据, 失败则记录日志
        DeviceDataVO deviceDataVO = new DeviceDataVO(iotDeviceDataVO, deviceId);
        // 获取当前日期, 并注入传感器数据 VO
        String curDate = DateUtil.getCurDateAndFormatted(dateFormatter);
        deviceDataVO.setCollectedDate(curDate);
        try {
            reportService.sendData(deviceDataVO);
        } catch (RestClientException | JsonProcessingException | IllegalArgumentException e) {
            logger.error(String.format("发送Iot设备数据失败%s", deviceDataVO.toString()), e);
        }
    }

    /**
     * 检查数据是否合法
     * @param message mqtt 消息
     * @return
     * @throws IotGatewayException
     */
    private IotDeviceDataVO checkMessageFormat(MqttMessage message) throws IotGatewayException {

        // 判断是否空数据
        if (ArrayUtils.isEmpty(message.getPayload())) {
            throw new IotGatewayException("得到空消息");
        }

        // 序列化数据
        IotDeviceDataVO iotDeviceDataVO;
        try {
            iotDeviceDataVO = JsonUtil.byteArrayToObject(message.getPayload(), IotDeviceDataVO.class);
        } catch (IotGatewayException e) {
            throw new IotGatewayException(String.format("IOT数据序列化失败, %s", message.getPayload()), e);
        }

        // 判断必填字段是否空白
        if (StringUtils.isAnyBlank(
            iotDeviceDataVO.getDeviceUuid(),
            iotDeviceDataVO.getParentUuid(),
            iotDeviceDataVO.getDeviceInformation(), 
            iotDeviceDataVO.getDeviceTag())) {

            throw new IotGatewayException(String.format("Iot数据不合法 %s", iotDeviceDataVO));
        }

        return iotDeviceDataVO;
    }

    /**
     * 检查设备是否注册
     * @param iotDeviceDataVO
     * @return
     * @throws RestClientException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     * @throws IotGatewayException
     */
    private Long checkIfRegist(IotDeviceDataVO iotDeviceDataVO) throws RestClientException, JsonProcessingException, IllegalArgumentException, IotGatewayException {

        // 有缓存则说明已注册
        Long deviceId = cacheAndMetricsService.getDeviceId(iotDeviceDataVO.getDeviceUuid());
        if (ObjectUtils.isNotEmpty(deviceId)) {
            return deviceId;
        }

        // 没有注册则发送注册请求
        DeviceVO deviceVO = new DeviceVO(iotDeviceDataVO, cacheAndMetricsService.getGatewayUuid());
        DeviceVO registDeviceVO = reportService.registDeviceVO(deviceVO);

        // 注册成功则放入缓存
        deviceId = registDeviceVO.getId();
        cacheAndMetricsService.put(iotDeviceDataVO.getDeviceUuid(), deviceId);

        return deviceId;
    }

    /**
     * 发送 Mqtt 消息
     * @param <T>
     * @param topic
     * @param object
     * @throws IotGatewayException
     */
    public <T> void sendMessage(String topic, T object) throws IotGatewayException {

        if (ObjectUtils.isEmpty(mqttClient)) {
            throw new IotGatewayException("Mqtt Client 未初始化");
        }

        if (ObjectUtils.isEmpty(object)) {
            throw new IotGatewayException("序列化对象为空");
        }


        if (StringUtils.isAnyBlank(topic)) {
            throw new IotGatewayException("Topic 为空");
        }
        
        MqttMessage mqttMessage = new MqttMessage(JsonUtil.toJsonString(object).getBytes());
        mqttMessage.setQos(1);
        try {
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            throw new IotGatewayException("消息发送失败", e);
        }
    }
}
