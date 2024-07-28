package com.njuzr.eaibackend.exception;

import com.njuzr.eaibackend.controller.MyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author: Leonezhurui
 * @Date: 2024/2/21 - 08:35
 * @Package: EAI-Backend
 * @Descrpition: 定义全局异常处理，是异常处理的最后一道防线；在业务逻辑中未处理的异常，会统一在这里被捕捉；
 * 如果需要捕捉特定异常，则需要在业务代码中指定。
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @PreAuthorize注解，使得抛出的Spring Security的AccessDeniedException不会被自定义的AccessDeniedHandler捕获，转而由Spring MVC的异常处理机制（如@ControllerAdvice）来处理的
    // 务必注意这里的异常类型：org.springframework.security.access.AccessDeniedException.class
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public MyResponse handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        log.error("全局异常处理器捕获，访问被拒绝，错误如下："+e.getMessage());
        return MyResponse.error(HttpStatus.FORBIDDEN.value(), "权限不够：" + e.getMessage());
    }

    /**
     * 处理@NotNull标注的字段为空的情况
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public MyResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("全局异常处理器捕获，@NotNull标注的字段为null，错误如下："+e.getMessage());
        return MyResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"传递信息缺失");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public MyResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("全局异常处理器捕获，请求体转换出错，错误如下"+"："+e.getMessage());
        return MyResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"传递信息错误");

    }


    @ExceptionHandler(MyException.class)
    public MyResponse handleGlobalException(MyException e) {
        log.error("全局异常处理器捕获，错误如下："+e.getMessage());
        return MyResponse.error(e.getErrCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public MyResponse handleGlobalException(Exception e) {
        log.error("全局异常处理器捕获，错误如下："+e.getMessage());
        return MyResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器错误： " + e.getMessage());
    }

}
