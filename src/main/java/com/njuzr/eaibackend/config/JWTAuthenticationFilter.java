package com.njuzr.eaibackend.config;

import com.njuzr.eaibackend.exception.MyAuthenticationEntryPoint;
import com.njuzr.eaibackend.exception.MyException;
import com.njuzr.eaibackend.service.MyUserDetailService;
import com.njuzr.eaibackend.utils.JWTTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/20 - 21:51
 * @Package: EAI-Backend
 */

@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenUtil jwtTokenUtil;
    private final MyUserDetailService userDetailService;

    private final MyAuthenticationEntryPoint authenticationEntryPoint = new MyAuthenticationEntryPoint();

    public JWTAuthenticationFilter(JWTTokenUtil jwtTokenUtil, MyUserDetailService userDetailService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        log.info("从Header中解析出Token：" + token);

        try {
            if (token != null) {
                String username = jwtTokenUtil.parseToken(token);
                log.info("解析出username：" + username);
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                log.info("解析出UserDetails：" + userDetails);

                // 设置principal和权限
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

            chain.doFilter(request, response);
         } catch (CredentialsExpiredException e) {
            // 将异常传递给AuthenticationEntryPoint处理（必要的步骤）
            authenticationEntryPoint.commence(request, response, e);
        }

    }
}
