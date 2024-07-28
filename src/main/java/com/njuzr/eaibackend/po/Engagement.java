package com.njuzr.eaibackend.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.njuzr.eaibackend.enums.AssignmentCompletionStatus;
import lombok.Data;

import java.util.Date;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/4 - 23:21
 * @Package: EAI-Backend
 */

@Data
@TableName("student_assignment")
public class Engagement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private Double score;
    private String remark;
    private String fileUrl; // 学生作业的oss链接
    private int version; // 文件版本，用于onlyoffice key的构建（解决文件不同步问题）
    private AssignmentCompletionStatus status; // 学生作业状态
}
