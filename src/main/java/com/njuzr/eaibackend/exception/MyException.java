package com.njuzr.eaibackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/15 - 09:05
 * @Package: EAI-Backend
 */

@Getter
public class MyException extends RuntimeException{
    private final int errCode;

    public MyException(int errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
    }

    public static MyException create(HttpStatus status, String msg) {
        return new MyException(status.value(), status.getReasonPhrase()+"："+msg);
    }
}
