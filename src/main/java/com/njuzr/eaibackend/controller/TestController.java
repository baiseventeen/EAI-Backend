package com.njuzr.eaibackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/14 - 22:14
 * @Package: EAI-Backend
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping
    public MyResponse test() {
        return MyResponse.success("测试成功了");
    }
}
