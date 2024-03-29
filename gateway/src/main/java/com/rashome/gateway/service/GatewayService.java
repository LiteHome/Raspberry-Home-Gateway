package com.rashome.gateway.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rashome.gateway.commons.enums.ResultCode;
import com.rashome.gateway.commons.exception.IotGatewayException;
import com.rashome.gateway.commons.util.HttpUtil;
import com.rashome.gateway.commons.util.JsonUtil;
import com.rashome.gateway.dto.DeviceDataVO;
import com.rashome.gateway.dto.DeviceVO;
import com.rashome.gateway.dto.IotDeviceDataVO;
import com.rashome.gateway.dto.ResultDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GatewayService {

    @Value("${backend.server.device.url}")
    private String deviceUrl;
        
    @Value("${backend.server.data.url}")
    private String deviceDataUrl;

    @Value("${gateway.tag}")
    private String gatewayTag;

    @Value("${gateway.info}")
    private String gatewayInfo;

    // map<UuidString, DeviceId>
    private final Map<String, Long> map = new ConcurrentHashMap<>(100);

    private String gatewayUuid;

    private Long gatewayDeviceId;

    private String gatewayRate;

    private static final String FILE_PATH = "device.config";

    // 初始化网关
    @EventListener(ApplicationReadyEvent.class)
    private void initGateway() throws IotGatewayException {

        // 获取 uuid
        this.gatewayUuid = this.getUuid();

        // 没有 rate, 则默认 30s 一次
        String gatewayRate = System.getProperty("gateway.rate");
        if(StringUtils.isBlank(gatewayRate)) {
            this.gatewayRate = "30s";
        } else {
            this.gatewayRate = gatewayRate;
        }

        log.info("开始注册网关");
        // 注册网关
        DeviceVO deviceVO = DeviceVO.builder()
            .deviceInformation(this.gatewayInfo)
            .deviceTag(this.gatewayTag)
            .healthCheckRate(this.gatewayRate)
            .deviceUuid(this.gatewayUuid)
            .parentUuid(this.gatewayUuid)
            .gatewayUuid(this.gatewayUuid)
            .build();
        this.gatewayDeviceId = this.registDeviceFromDeviceVO(deviceVO);
        log.info("注册网关成功");
    }

    // gateway uuid 写入配置文件
    private void writeToPropertiesFile(String deviceUuid) throws IotGatewayException {
        JsonUtil.writeToFile(DeviceVO.builder().deviceUuid(deviceUuid).build(), FILE_PATH);
    }

    // 从配置文件获取 uuid, 没有则生成 uuid, 持久化到本地并返回
    private String getUuid() throws IotGatewayException {
        try {
            DeviceVO localDeviceVOWithUuid = JsonUtil.readFromFile(DeviceVO.class, FILE_PATH);
            log.info("从本地获取 uuid");
            return localDeviceVOWithUuid.getDeviceUuid();
        } catch (IotGatewayException e) {
            log.info("无法从本地获取 uuid", e);
        }
        
        String uuid = UUID.randomUUID().toString();
        this.writeToPropertiesFile(uuid);
        return uuid;
    }

    /**
     * 发送传感器数据
     * @param deviceDataVO
     * @throws IotGatewayException
     */
    public void sendData(IotDeviceDataVO iotDeviceDataVO) throws IotGatewayException {

        // 注册设备
        Long deviceId;
        try {
            deviceId = registDeviceFromIotDeviceDataVO(iotDeviceDataVO);
        } catch (IotGatewayException e) {
            if (iotDeviceDataVO.getParentUuid().equals(iotDeviceDataVO.getDeviceUuid())) {
                throw new IotGatewayException("注册控制板失败", e);
            } else {
                throw new IotGatewayException("注册传感器失败", e);
            }
        }

        // 复制传感器数据
        DeviceDataVO deviceDataVO = new DeviceDataVO();
        BeanUtils.copyProperties(iotDeviceDataVO, deviceDataVO);
        // 设置设备id
        deviceDataVO.setDeviceId(deviceId);


        // 发送数据
        String payload = JsonUtil.toJsonString(deviceDataVO);
        // log.info(String.format("发送的数据是 %s", payload));
        HttpUtil.postJsonPayload(deviceDataUrl, payload);
    }
    
    /**
     * 注册设备, 如果抛异常说明注册失败. 
     * 首先检查有没有缓存, 没有则注册并更新, 有则返回 device id
     * @param deviceVO
     * @return
     * @throws RestClientException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     * @throws IotGatewayException
     */
    private Long registDeviceFromIotDeviceDataVO(IotDeviceDataVO iotDeviceDataVO) throws IotGatewayException {
        // 新建 device vo, 设置网关的 uuid, 并注册
        DeviceVO registDeviceVO = new DeviceVO(iotDeviceDataVO, this.gatewayUuid);
        return registDeviceFromDeviceVO(registDeviceVO);
    }

        /**
     * 注册设备, 如果抛异常说明注册失败. 
     * 首先检查有没有缓存, 没有则注册并更新, 有则返回 device id
     * @param deviceVO
     * @return
     * @throws RestClientException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     * @throws IotGatewayException
     */
    private Long registDeviceFromDeviceVO(DeviceVO deviceVO) throws IotGatewayException {

        if (this.map.containsKey(deviceVO.getDeviceUuid())) {
            return map.get(deviceVO.getDeviceUuid());
        }
        

        ResultDTO result;
        try {
            String payload = JsonUtil.toJsonString(deviceVO);
            String response = HttpUtil.postJsonPayload(deviceUrl, payload);
            result = JsonUtil.stringToObject(response, ResultDTO.class);
        } catch (IotGatewayException e) {
            throw new IotGatewayException("注册设备失败", e);
        }

        if (!StringUtils.equalsIgnoreCase(result.getCode(), ResultCode.SUCCESSED.getCode()) || StringUtils.isBlank(result.getData())) {
            throw new IotGatewayException(String.format("注册设备失败, 远端返回错误码或者数据为空, %s, %s", ResultCode.valueOf(result.getCode()), result.getMessage()));
        }

        return JsonUtil.stringToObject(result.getData(), DeviceVO.class).getId();
    }
}
