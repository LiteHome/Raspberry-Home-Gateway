package com.rashome.gateway.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送到后端服务器的数据 VO
 */
@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
public class DeviceDataVO {

    // timestamp without offset
    @JsonProperty(value = "collected_date")
    private Long collectedDate = System.currentTimeMillis();

    @JsonProperty(value = "device_id")
    private Long deviceId;

    private Map<String, Object> body = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getBody(){
        return this.body;
    }

    @JsonAnySetter
    public void setBody(String key, Object value){
        this.body.put(key, value);
    }
}
