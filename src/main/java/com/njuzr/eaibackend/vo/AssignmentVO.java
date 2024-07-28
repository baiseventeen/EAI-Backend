package com.njuzr.eaibackend.vo;

import com.njuzr.eaibackend.enums.AssignmentStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/15 - 00:42
 * @Package: EAI-Backend
 */

@Data
public class AssignmentVO {
    private Long assignmentId;

    private String assignmentName;

    private String description;

    private String descriptionFile; // 作业要求，Word/Pdf文件

    private List<String> attachments; // 作业附件

    private Long teacherId; // 发布作业的老师的Id

    private Long courseId; // 哪个课程下的assignment

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private AssignmentStatus status; // 根据startTime和endTime计算出来的status，共有三种：未开始、进行中、已结束
}
