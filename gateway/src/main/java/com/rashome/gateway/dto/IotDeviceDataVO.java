package com.rashome.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rashome.gateway.dto.base.BaseDataVO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 接收来自 iot 设备的数据 VO
 */
@JsonInclude(Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class IotDeviceDataVO extends BaseDataVO {

    @JsonProperty(value = "parent_uuid")
    private String parentUuid;

    // 默认值是 30s
    @JsonProperty(value = "health_check_rate")
    private String healthCheckRate = "30";

    @JsonProperty(value = "device_information")
    private String deviceInformation;

    @JsonProperty(value = "device_uuid")
    private String deviceUuid;

    @JsonProperty(value = "gateway_uuid")
    private String gatewayUuid;

    @JsonProperty(value = "device_tag")
    private String deviceTag;
}
