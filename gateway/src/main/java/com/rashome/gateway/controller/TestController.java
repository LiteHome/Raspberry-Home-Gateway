package com.rashome.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    

    /**
     * 测试接口
     * @return List<DeviceVO> 全部设备VO
     */
    @GetMapping("/")
    public String getDevices(){
        return "hello";
    }
}
