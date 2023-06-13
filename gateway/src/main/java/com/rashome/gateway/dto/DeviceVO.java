package com.rashome.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 发送到后端服务器的设备 VO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class DeviceVO {

    private Long id;


    @JsonProperty(value = "health_check_url")
    private String healthCheckUrl;

    // 默认值是 30s
    @JsonProperty(value = "health_check_rate")
    private String healthCheckRate;

    @JsonProperty(value = "device_information")
    private String deviceInformation;

    @JsonProperty(value = "device_uuid")
    private String deviceUuid;

    @JsonProperty(value = "device_tag")
    private String deviceTag;

    @JsonProperty(value = "parent_uuid")
    private String parentUuid;

    @JsonProperty(value = "gateway_uuid")
    private String gatewayUuid;
    
    public DeviceVO(IotDeviceDataVO iotDeviceDataVO, String gatewayUuid) {

        this.deviceInformation = iotDeviceDataVO.getDeviceInformation();
        this.deviceUuid = iotDeviceDataVO.getDeviceUuid();
        this.deviceTag = iotDeviceDataVO.getDeviceTag();
        this.parentUuid = iotDeviceDataVO.getParentUuid();
        this.gatewayUuid = gatewayUuid;
    }


}
