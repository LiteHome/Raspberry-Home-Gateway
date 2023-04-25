package com.rashome.gateway.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;


public class BaseDataVO {

    public Float temperature;

    public Float humidity;

    @JsonProperty(value = "camera_image_base64")
    public String cameraImageBase64;

    @JsonProperty(value = "success_request_latency_avg")
    public Float successRequestLatencyAvg;

    @JsonProperty(value = "fail_request_latency_avg")
    public Float failRequestLatencyAvg;

    @JsonProperty(value = "fail_fetch_sensor_data_count_avg")
    public Float failFetchSensorDataCountAvg;
}
