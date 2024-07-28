package com.njuzr.eaibackend.config;

import com.njuzr.eaibackend.exception.MyAccessDeniedHandler;
import com.njuzr.eaibackend.exception.MyAuthenticationEntryPoint;
import com.njuzr.eaibackend.service.MyUserDetailService;
import com.njuzr.eaibackend.utils.JWTTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/19 - 16:28
 * @Package: EAI-Backend
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final MyAuthenticationEntryPoint unauthorizedHandler;

    private final MyAccessDeniedHandler accessDeniedHandler;

    private final MyUserDetailService userDetailsService;

    private final JWTTokenUtil jwtTokenUtil;

    @Autowired
    public SecurityConfig(MyAuthenticationEntryPoint unauthorizedHandler, MyAccessDeniedHandler accessDeniedHandler, MyUserDetailService userDetailsService, JWTTokenUtil jwtTokenUtil) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.accessDeniedHandler = accessDeniedHandler;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/user/login", "/api/user/register", "/api/user/verifyCode", "/api/onlyoffice/**", "/api/assignment/**").permitAll() // 允许公开访问的路径
                        .anyRequest().authenticated() // 其他所有请求需要认证
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)) // 认证失败处理（未提供认证信息）
                .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler)) // 权限不够
                .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF
                .formLogin(AbstractHttpConfigurer::disable) // 禁用登陆表单
                .httpBasic(AbstractHttpConfigurer::disable) // 禁用http basic
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 设置为无状态

        // 获取AuthenticationManager
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

        // 配置UsernamePasswordAuthenticationFilter
        UsernamePasswordAuthenticationFilter authenticationFilter = new UsernamePasswordAuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager);

        // 配置自定义认证成功和失败处理器
        authenticationFilter.setAuthenticationSuccessHandler(new MyAuthenticationSuccessHandler());
        authenticationFilter.setAuthenticationFailureHandler(new MyAuthenticationFailureHandler());

        // JWT过滤器（在过滤器中已经实现了Token解析，不通过Provider解析）
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(jwtTokenUtil, userDetailsService);

        // 将自定义的Filter添加到Spring Security过滤器链中
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilter(authenticationFilter);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
