package com.njuzr.eaibackend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.njuzr.eaibackend.controller.MyResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/21 - 07:43
 * @Package: EAI-Backend
 */

@Slf4j
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        log.error("认证出错，用户权限不够");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        MyResponse myResponse = MyResponse.error(HttpServletResponse.SC_FORBIDDEN, "Authentication forbidden: " + exception.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(myResponse));
        response.getWriter().flush();
    }
}
