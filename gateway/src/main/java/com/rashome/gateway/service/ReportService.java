package com.rashome.gateway.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.rashome.gateway.commons.enums.ResultCode;
import com.rashome.gateway.commons.exception.IotGatewayException;
import com.rashome.gateway.commons.util.HttpUtil;
import com.rashome.gateway.dto.DeviceDataVO;
import com.rashome.gateway.dto.DeviceVO;
import com.rashome.gateway.dto.ResultDTO;

@Service
public class ReportService {

    @Value("${backend.server.device.url}")
    private String deviceUrl;

        
    @Value("${backend.server.data.url}")
    private String deviceDataUrl;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final TypeFactory TYPE_FACTORY = OBJECT_MAPPER.getTypeFactory();

    /**
     * 发送传感器数据, 丢了就丢了
     * @param deviceDataVO
     * @throws RestClientException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     * @throws IotGatewayException
     */
    public void sendData(DeviceDataVO deviceDataVO) throws RestClientException, JsonProcessingException, IllegalArgumentException, IotGatewayException {
        HttpUtil.postJsonPayload(deviceDataUrl, OBJECT_MAPPER.writeValueAsString(deviceDataVO));
    }
    
    /**
     * 注册设备, 如果抛异常说明注册失败
     * @param deviceVOList
     * @return
     * @throws RestClientException
     * @throws JsonProcessingException
     * @throws IllegalArgumentException
     * @throws IotGatewayException
     */
    public List<DeviceVO> registDeviceVO(List<DeviceVO> deviceVOList) throws RestClientException, JsonProcessingException, IllegalArgumentException, IotGatewayException {
        
        String response = HttpUtil.postJsonPayload(deviceUrl, OBJECT_MAPPER.writeValueAsString(deviceVOList));

        ResultDTO result = OBJECT_MAPPER.readValue(response, ResultDTO.class);

        if (result.getCode() != ResultCode.SUCCESSED.getCode()) {
            throw new IotGatewayException(String.format("%s, %s", ResultCode.valueOf(result.getCode()), result.getMessage()));
        }

        return OBJECT_MAPPER.readValue(result.getData(), TYPE_FACTORY.constructCollectionType(List.class, DeviceVO.class));
    }
}
