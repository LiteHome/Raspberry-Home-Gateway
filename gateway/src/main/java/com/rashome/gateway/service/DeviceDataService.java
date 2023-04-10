package com.rashome.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rashome.gateway.commons.exception.IotGatewayException;
import com.rashome.gateway.commons.util.HttpUtil;
import com.rashome.gateway.dto.DeviceDataVO;

@Service
public class DeviceDataService {
    
    @Value("${backend.server.data.url}")
    private String url;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void sendData(DeviceDataVO deviceDataVO) throws RestClientException, JsonProcessingException, IllegalArgumentException, IotGatewayException {
        HttpUtil.postJsonPayload(url, OBJECT_MAPPER.writeValueAsString(deviceDataVO));
    }
}
