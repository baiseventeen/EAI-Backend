package com.njuzr.eaibackend.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/13 - 09:15
 * @Package: EAI-Backend
 */

@Configuration
public class OssConfig {
    @Value("oss-cn-shanghai.aliyuncs.com")
    private String endpoint;

    @Value("LTAI5tKsfJJKByjVqNKCvRR3")
    private String accessKeyId;

    @Value("fckFhhxhG234aOZfjIAxZZuFA3RCkH")
    private String accessKeySecret;

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
