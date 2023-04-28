package com.rashome.gateway.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class CacheAndMetricsService {
    
    private static final Map<String, Long> map = new ConcurrentHashMap<>(100);

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
