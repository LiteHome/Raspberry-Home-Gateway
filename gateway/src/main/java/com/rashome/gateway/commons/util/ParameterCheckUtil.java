package com.rashome.gateway.commons.util;

import org.apache.commons.lang3.StringUtils;

public class ParameterCheckUtil {

    public static Boolean isStringNumeric(String parameter) {
        String trimedParameter = StringUtils.trimToNull(parameter);
        return StringUtils.isNotEmpty(trimedParameter) && StringUtils.isNumeric(trimedParameter);
    }

    public static Boolean isStringEmpty(String parameter){
        return StringUtils.isBlank(parameter);
    }
}
