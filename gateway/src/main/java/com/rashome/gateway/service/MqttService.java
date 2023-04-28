package com.rashome.gateway.service;

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
import com.rashome.gateway.commons.util.JsonUtil;
import com.rashome.gateway.dto.DeviceDataVO;
import com.rashome.gateway.dto.DeviceVO;
import com.rashome.gateway.dto.IotDeviceDataVO;

@Service
public class MqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    private IMqttClient mqttClient;

    @Autowired
    private CacheAndMetricsService uuidMapService;

    @Autowired
    private ReportService reportService;



    public MqttService(@Value("${mqtt.broker.url}") String mqttBrokerUrl) {

        try {
            mqttClient = new MqttClient(mqttBrokerUrl, UUID.randomUUID().toString(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            mqttClient.connect(options);
        } catch (MqttException e) {
            logger.error("Mqtt 连接失败", new IotGatewayException(e));
        }

        try {
            mqttClient.subscribe("board/+/data", this::messageHandler);
            mqttClient.subscribe("board/+/sensor/+/data", this::messageHandler);
        } catch (MqttException e) {
            logger.error("Mqtt 订阅失败", new IotGatewayException(e));
        }
    }

    private void messageHandler(String topic, MqttMessage message) {

        boolean isSensorIotDeviceData = true;

        IotDeviceDataVO iotDeviceDataVO;
        try {
            iotDeviceDataVO = checkMessageFormat(message);
        } catch (IotGatewayException e) {
            logger.warn("Iot 设备消息处理失败", e);
            return;
        }

        if (iotDeviceDataVO.getParentUuid().equals(iotDeviceDataVO.getDeviceUuid())) {
            isSensorIotDeviceData = false;
        }

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

        DeviceDataVO deviceDataVO = new DeviceDataVO(iotDeviceDataVO, deviceId);

        try {
            reportService.sendData(deviceDataVO);
        } catch (RestClientException | JsonProcessingException | IllegalArgumentException e) {
            logger.error(String.format("发送Iot设备数据失败%s", deviceDataVO.toString()), e);
        }
    }

    private IotDeviceDataVO checkMessageFormat(MqttMessage message) throws IotGatewayException {

        if (ArrayUtils.isEmpty(message.getPayload())) {
            throw new IotGatewayException("得到空消息");
        }

        IotDeviceDataVO iotDeviceDataVO;
        try {
            iotDeviceDataVO = JsonUtil.byteArrayToObject(message.getPayload(), IotDeviceDataVO.class);
        } catch (IotGatewayException e) {
            throw new IotGatewayException(String.format("IOT数据序列化失败, %s", message.getPayload()), e);
        }

        if (StringUtils.isAnyBlank(
            iotDeviceDataVO.getDeviceUuid(),
            iotDeviceDataVO.getParentUuid(),
            iotDeviceDataVO.getDeviceInformation(), 
            iotDeviceDataVO.getDeviceTag())) {
        
            throw new IotGatewayException(String.format("Iot数据不合法 %s", iotDeviceDataVO));
        }

        return iotDeviceDataVO;
    }

    private Long checkIfRegist(IotDeviceDataVO iotDeviceDataVO) throws RestClientException, JsonProcessingException, IllegalArgumentException, IotGatewayException {

        Long deviceId = uuidMapService.getDeviceId(iotDeviceDataVO.getDeviceUuid());

        if (ObjectUtils.isNotEmpty(deviceId)) {
            return deviceId;
        }

        DeviceVO deviceVO = new DeviceVO(iotDeviceDataVO);

        DeviceVO registDeviceVO = reportService.registDeviceVO(deviceVO);

        deviceId = registDeviceVO.getId();

        uuidMapService.put(iotDeviceDataVO.getDeviceUuid(), deviceId);

        return deviceId;
    }

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
