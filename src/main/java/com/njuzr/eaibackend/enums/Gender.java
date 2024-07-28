package com.njuzr.eaibackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/14 - 22:03
 * @Package: EAI-Backend
 */

public enum Gender {
    MAN(0, "MAN"),
    WOMAN(1, "WOMAN");

    @EnumValue
    private final int code;
    private final String gender;

    Gender(int code, String gender) {
        this.code = code;
        this.gender = gender;
    }

    public int getCode() {
        return code;
    }

    public String getGender() {
        return gender;
    }
}
