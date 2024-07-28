package com.njuzr.eaibackend.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/19 - 11:44
 * @Package: EAI-Backend
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {
    //设置允许访问的来源
    String[] origins = new String[]{
            "http://localhost:3000",
            "http://localhost:3001",
            "http://127.0.0.1:4000",
            "http://127.0.0.1:4001",
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有路径应用规则
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的请求方法
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true) // 允许发送Cookies
                .maxAge(3600); // 预检请求的缓存时间（秒）
    }
}
