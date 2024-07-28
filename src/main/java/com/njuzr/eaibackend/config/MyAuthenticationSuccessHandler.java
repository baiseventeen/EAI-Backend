package com.njuzr.eaibackend.config;

import com.njuzr.eaibackend.controller.MyResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/19 - 23:20
 * @Package: EAI-Backend
 * 认证成功处理器
 */

public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK); // 状态码200
        response.setContentType("application/json");
        MyResponse myResponse = MyResponse.success(authentication.getPrincipal());
        response.getWriter().write(objectMapper.writeValueAsString(myResponse));
        response.getWriter().flush();
    }
}
