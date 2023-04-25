package com.rashome.gateway.service;

import java.io.IOException;
import java.util.UUID;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rashome.gateway.commons.exception.IotGatewayException;
import com.rashome.gateway.dto.IotDeviceDataVO;

@Service
public class MqttService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    private IMqttClient mqttClient;

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
        
        try {
            IotDeviceDataVO iotDeviceDataVO = OBJECT_MAPPER.readValue(message.getPayload(), IotDeviceDataVO.class);
            logger.info(iotDeviceDataVO.toString());
        } catch (IOException e) {
            logger.error("JSON 反序列化失败", new IotGatewayException(e));
            return;
        }
    }

    public void sendMessage(String topic, String payload) throws IotGatewayException {

        if (ObjectUtils.isEmpty(mqttClient)) {
            throw new IotGatewayException("Mqtt Client 未初始化");
        }


        if (StringUtils.isAnyBlank(topic, payload)) {
            throw new IotGatewayException("Topic, Payload 为空");
        }
        
        MqttMessage mqttMessage = new MqttMessage(payload.getBytes());
        mqttMessage.setQos(1);
        try {
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            throw new IotGatewayException("消息发送失败", e);
        }
    }
}
