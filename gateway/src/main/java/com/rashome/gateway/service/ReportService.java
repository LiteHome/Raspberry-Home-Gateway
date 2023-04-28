package com.rashome.gateway.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rashome.gateway.commons.enums.ResultCode;
import com.rashome.gateway.commons.exception.IotGatewayException;
import com.rashome.gateway.commons.util.HttpUtil;
import com.rashome.gateway.commons.util.JsonUtil;
import com.rashome.gateway.dto.DeviceDataVO;
import com.rashome.gateway.dto.DeviceVO;
import com.rashome.gateway.dto.ResultDTO;

@Service
public class ReportService {

    @Value("${backend.server.device.url}")
    private String deviceUrl;

        
    @Value("${backend.server.data.url}")
    private String deviceDataUrl;

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    /**
     * 发送传感器数据
     * @param deviceDataVO
     * @throws RestClientException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     * @throws IotGatewayException
     */
    public void sendData(DeviceDataVO deviceDataVO) throws RestClientException, JsonProcessingException, IllegalArgumentException {

        String payload;
        try {
            payload = JsonUtil.toJsonString(deviceDataVO);
            HttpUtil.postJsonPayload(deviceDataUrl, payload);
        } catch (IotGatewayException e) {
            logger.warn(String.format("发送设备数据失败%s", deviceDataVO), e);
            return;
        }
    }
    
    /**
     * 注册设备, 如果抛异常说明注册失败
     * @param deviceVO
     * @return
     * @throws RestClientException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     * @throws IotGatewayException
     */
    public DeviceVO registDeviceVO(DeviceVO deviceVO) throws RestClientException, JsonProcessingException, IllegalArgumentException, IotGatewayException {
        
        ResultDTO result;
        try {
            String payload = JsonUtil.toJsonString(deviceVO);
            String response = HttpUtil.postJsonPayload(deviceUrl, payload);
            result = JsonUtil.stringToObject(response, ResultDTO.class);
        } catch (IotGatewayException e) {
            throw new IotGatewayException(String.format("注册设备失败, 输入格式有误 %s", deviceVO.toString()), e);
        }

        if (!StringUtils.equalsIgnoreCase(result.getCode(), ResultCode.SUCCESSED.getCode()) || StringUtils.isBlank(result.getData())) {
            throw new IotGatewayException(String.format("注册设备失败, 远端返回错误码或者数据为空, %s, %s", ResultCode.valueOf(result.getCode()), result.getMessage()));
        }

        return JsonUtil.stringToObject(result.getData(), DeviceVO.class);
    }
}
