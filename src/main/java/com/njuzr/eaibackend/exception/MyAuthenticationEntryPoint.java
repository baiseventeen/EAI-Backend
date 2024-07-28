package com.njuzr.eaibackend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.njuzr.eaibackend.controller.MyResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/19 - 16:56
 * @Package: EAI-Backend
 */


@Slf4j
@Component
// 用于处理那些需要认证的请求但是用户未提供任何认证信息的情况
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        MyResponse myResponse;
        if (authException instanceof CredentialsExpiredException) { // token过期的异常处理
            log.error("认证出错，用户token已过期");
            myResponse = MyResponse.error(HttpServletResponse.SC_UNAUTHORIZED, "token已过期：" + authException.getMessage());
        } else {
            log.error("认证出错，用户未提供认证信息");
            myResponse = MyResponse.error(HttpServletResponse.SC_UNAUTHORIZED, "用户未授权：" + authException.getMessage());
        }
        response.getWriter().write(mapper.writeValueAsString(myResponse));
    }
}
