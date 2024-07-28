package com.njuzr.eaibackend.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/14 - 23:52
 * @Package: EAI-Backend
 */

@Data
@AllArgsConstructor
public class MyResponse {
    private int code;
    private String msg;
    private Object data;

    public static MyResponse success(Object data) {
        return new MyResponse(200, "Success", data);
    }

    public static MyResponse error(int errCode, String errMsg) {
        return new MyResponse(errCode, errMsg, null);
    }
}
