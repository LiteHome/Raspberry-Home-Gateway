package com.rashome.gateway.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rashome.gateway.commons.exception.IotGatewayException;
import com.rashome.gateway.dto.DeviceVO;

@Service
public class CacheAndMetricsService {
    
    private static final Map<String, Long> map = new ConcurrentHashMap<>(100);

    private String gatewayUuid;

    private Long gatewayDeviceid;

    /**
     * 初始化函数, 将 gateway 注册到云端
     * @throws IotGatewayException
     * @throws IllegalArgumentException
     * @throws JsonProcessingException
     * @throws RestClientException
     */
    @Lazy
    @Autowired
    public CacheAndMetricsService(ReportService reportService) throws RestClientException, JsonProcessingException, IllegalArgumentException, IotGatewayException {

        String uuidString = UUID.randomUUID().toString();
        
        DeviceVO deviceVO = new DeviceVO();

        deviceVO.setDeviceInformation("树莓派网关");
        deviceVO.setDeviceTag("2012");
        deviceVO.setDeviceUuid(uuidString);
        deviceVO.setParentUuid(uuidString);
        deviceVO.setGatewayUuid(uuidString);
        
        DeviceVO registDeviceVO = reportService.registDeviceVO(deviceVO);

        this.gatewayUuid = uuidString;
        this.gatewayDeviceid = registDeviceVO.getId();
    }

    public String getGatewayUuid() {
        return this.gatewayUuid;
    }

    public Long getGatewayDeviceId() {
        return this.gatewayDeviceid;
    }

    public Long getDeviceId(String uuid) {

        if (map.containsKey(uuid)) {
            return map.get(uuid);
        } else {
            return null;
        }

    }

    public void put(String uuid, Long deviceId) {
        map.put(uuid, deviceId);
    }
}
