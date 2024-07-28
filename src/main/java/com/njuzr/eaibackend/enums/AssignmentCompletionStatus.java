package com.njuzr.eaibackend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/4 - 22:55
 * @Package: EAI-Backend
 */

@Getter
public enum AssignmentCompletionStatus {
    NOT_EDITED(0, "NOT_EDITED"), // 待完成，还未进行写作页面进行编辑
    NOT_SUBMITTED(1, "NOT_SUBMITTED"), // 未提交，正在完成作业中
    SUBMITTED(2, "SUBMITTED"), // 已提交，完成编辑并提交内容
    NOT_DONE(3, "NOT_DONE"), // 未完成，到了DDL，仍然没有提交的
    PUBLISHED(4, "PUBLISHED"), // 老师已批改并发布
    NOT_CORRECTED(5, "NOT_CORRECTED"), //未批改（状态仅老师可见）
    CORRECTED(6, "CORRECTED") //已批改、但还未发布（状态仅老师可见）
    ;

    @EnumValue
    private final int code;
    private final String status;

    AssignmentCompletionStatus(int code, String status) {
        this.code = code;
        this.status = status;
    }

}
