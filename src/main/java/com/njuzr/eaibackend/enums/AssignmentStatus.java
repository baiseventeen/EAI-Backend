package com.njuzr.eaibackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/17 - 10:30
 * @Package: EAI-Backend
 */

public enum AssignmentStatus {
    NOT_STARTED(0, "NOT_STARTED"),
    PROCEEDING(1, "PROCEEDING"),
    FINISHED(2, "FINISHED")
    ;

    @EnumValue
    private final int code;
    private final String status;

    AssignmentStatus(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}
