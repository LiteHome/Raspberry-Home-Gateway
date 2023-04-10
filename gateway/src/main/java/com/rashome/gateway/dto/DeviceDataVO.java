package com.rashome.gateway.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;


@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
public class DeviceDataVO {

    @JsonProperty(value = "collected_date")
    private Date collectedDate;

    @JsonProperty(value = "device_id")
    private Long deviceId;

    private Float temperature;

    private Float humidity;

    @JsonProperty(value = "camera_image_url")
    private Float cameraImageUrl;

    @JsonProperty(value = "success_request_latency_avg")
    private Float successRequestLatencyAvg;

    @JsonProperty(value = "fail_request_latency_avg")
    private Float failRequestLatencyAvg;

    @JsonProperty(value = "fail_fetch_sensor_data_count_avg")
    private Float failFetchSensorDataCountAvg;
}
