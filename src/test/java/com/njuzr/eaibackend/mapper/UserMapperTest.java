package com.njuzr.eaibackend.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/15 - 10:23
 * @Package: EAI-Backend
 */

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;


    @Test
    @DisplayName("根据ID查找用户")
    void findById() {

    }

    @Test
    @DisplayName("创建用户")
    void insert() {

    }
}
