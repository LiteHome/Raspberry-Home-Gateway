package com.rashome.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rashome.gateway.commons.util.DateUtil;
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

    // timestamp without offset
    @JsonProperty(value = "collected_date")
    private String collectedDate = DateUtil.getCurTimestampAsString();

    @JsonProperty(value = "device_id")
    private Long deviceId;
}
