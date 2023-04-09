package com.rashome.gateway.commons.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.rashome.gateway.commons.exception.IotGatewayException;

/**
 * Http 请求工具类
 */
public class HttpUtil {
    
    private static final RestTemplate restTemplate = new RestTemplate();


    /**
     * post json 请求
     * @param url
     * @param payload
     * @return
     * @throws IotGatewayException
     * @throws RestClientException
     * @throws IllegalArgumentException
     */
    public static String postJsonPayload(String url, String payload) throws IotGatewayException, RestClientException, IllegalArgumentException {
        
        HttpHeaders httpHeaders = new HttpHeaders();

        // 设置请求头部
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(payload, httpHeaders);

        // 发送请求
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

        // 判断请求是否成功
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw IotGatewayException.parametersInMessage("Http 请求失败, 请求 Url 是 %s, 请求 payload 是 %s", url, payload);
        }

        return responseEntity.getBody();

    }
}
