package com.njuzr.eaibackend;

import com.njuzr.eaibackend.config.OssConfig;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAutoConfiguration
//@MapperScan("com.njuzr.eaibackend.mapper")
public class EaiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EaiBackendApplication.class, args);
    }

}
