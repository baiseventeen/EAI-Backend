package com.njuzr.eaibackend.vo;

import com.njuzr.eaibackend.enums.AssignmentCompletionStatus;
import lombok.Data;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/17 - 20:30
 * @Package: EAI-Backend
 */

@Data
public class EngagementVO {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private Double score;
    private String remark;
    private String fileUrl; // 学生作业的oss链接
    private int version; // 文件版本，用于onlyoffice key的构建（解决文件不同步问题）
    private AssignmentCompletionStatus status; // 学生作业状态
    private String fileKey;
}
