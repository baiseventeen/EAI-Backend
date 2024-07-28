package com.njuzr.eaibackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/14 - 21:51
 * @Package: EAI-Backend
 */

public enum Role {
    ADMIN(0, "ADMIN"),
    STUDENT(1, "STUDENT"),
    TEACHER(2, "TEACHER");

    @EnumValue
    private final int code;
    private final String role;

    Role(int code, String role) {
        this.code = code;
        this.role = role;
    }

    public int getCode() {
        return code;
    }

    public String getRole() {
        return role;
    }
}
