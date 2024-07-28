package com.njuzr.eaibackend.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/4 - 22:21
 * @Package: EAI-Backend
 */


@Data
@TableName("assignment")
public class Assignment {
    @TableId(type = IdType.AUTO)
    private Long assignmentId;

    private Long courseId; // 外键！

    private String assignmentName;

    private String description;

    private String descriptionFile; // 作业要求，Word/Pdf文件

    private String attachments; // 作业附件

    private Long teacherId; // 发布作业的老师的Id

    private Date startTime;

    private Date endTime;

    private Date createTime;
}
