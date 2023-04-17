package com.rashome.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceVO {

    public Long id;

    @JsonProperty(value = "health_check_url")
    private String healthCheckUrl;

    // 默认值是 30s
    @JsonProperty(value = "health_check_rate")
    private String healthCheckRate = "30";

    @JsonProperty(value = "device_name")
    private String deviceName;

    @JsonProperty(value = "device_information")
    private String deviceInformation;

    @JsonProperty(value = "device_uccid")
    private String deviceUccid;
}
