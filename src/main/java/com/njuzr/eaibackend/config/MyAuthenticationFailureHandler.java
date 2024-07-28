package com.njuzr.eaibackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.njuzr.eaibackend.controller.MyResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/19 - 23:24
 * @Package: EAI-Backend
 * 认证失败处理器
 */

public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        MyResponse myResponse = MyResponse.error(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + exception.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(myResponse));
        response.getWriter().flush();
    }
}
