package com.rashome.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接收来自 iot 设备的数据 VO
 */
@Data
@NoArgsConstructor
public class IotDeviceDataVO {
    @JsonProperty(value = "device_uuid")
    private String deviceUuid;

    @JsonProperty(value = "parent_uuid")
    private String parentUuid;

    @JsonProperty(value = "gateway_uuid")
    private String gatewayUuid;

    @JsonProperty(value = "device_tag")
    private String deviceTag;

    @JsonProperty(value = "device_information")
    private String deviceInformation;

    @JsonProperty(value = "body")
    private String body;

    // 默认值是 30s
    @JsonProperty(value = "health_check_rate")
    private String healthCheckRate;
}
