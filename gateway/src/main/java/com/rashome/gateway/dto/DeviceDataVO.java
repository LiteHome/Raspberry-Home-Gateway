package com.rashome.gateway.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rashome.gateway.dto.base.BaseDataVO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 发送到后端服务器的数据 VO
 */
@JsonInclude(Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class DeviceDataVO extends BaseDataVO {

    @JsonProperty(value = "collected_date")
    private Date collectedDate;

    @JsonProperty(value = "device_id")
    private Long deviceId;

    public DeviceDataVO(IotDeviceDataVO iotDeviceDataVO, Long deviceId) {

        this.collectedDate = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).getTime();
        this.deviceId = deviceId;

        this.temperature = iotDeviceDataVO.getTemperature();
        this.humidity = iotDeviceDataVO.getHumidity();
        this.cameraImageBase64 = iotDeviceDataVO.getCameraImageBase64();
        this.failFetchSensorDataCountAvg = iotDeviceDataVO.getFailFetchSensorDataCountAvg();
    }
}
