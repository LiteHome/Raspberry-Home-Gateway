package com.rashome.gateway.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResultCode {

    SUCCESSED("200"),

    DUPLICATE_DEVICE_NAME("401"),
    
    DEVICE_NOT_REGISTED("402");

    @Getter
    private final String code;

}
