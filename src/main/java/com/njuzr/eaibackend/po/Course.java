package com.njuzr.eaibackend.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/4 - 22:21
 * @Package: EAI-Backend
 */

@Data
@TableName("course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long courseId;

    private String courseName;

    @TableField("teacher_ids")
    private String teacherIds; // 序列化好的字符串

    @TableField("enroll_code")
    private String enrollCode; //选课码

    @TableField("course_imgs")
    private String courseImages; // 序列化好的字符串

    private String targetGrade;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private String description;

}
