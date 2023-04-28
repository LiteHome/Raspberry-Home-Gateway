package com.rashome.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 发送到后端服务器的设备 VO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class DeviceVO {

    private Long id;


    @JsonProperty(value = "health_check_url")
    private String healthCheckUrl;

    // 默认值是 30s
    @JsonProperty(value = "health_check_rate")
    private String healthCheckRate = "30";

    @JsonProperty(value = "device_information")
    private String deviceInformation;

    @JsonProperty(value = "device_uuid")
    private String deviceUuid;

    @JsonProperty(value = "device_tag")
    private String deviceTag;
    
    public DeviceVO(IotDeviceDataVO iotDeviceDataVO) {

        this.deviceInformation = iotDeviceDataVO.getDeviceInformation();
        this.deviceUuid = iotDeviceDataVO.getDeviceUuid();
        this.deviceTag = iotDeviceDataVO.getDeviceTag();

    }


}
