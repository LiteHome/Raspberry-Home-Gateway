package com.rashome.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResultDTO {
    
    private String code;
    
    private String message;

    private String data;
}
