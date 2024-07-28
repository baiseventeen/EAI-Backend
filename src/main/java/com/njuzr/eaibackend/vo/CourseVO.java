package com.njuzr.eaibackend.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.njuzr.eaibackend.po.Course;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/5 - 22:10
 * @Package: EAI-Backend
 */

@Data
public class CourseVO { // 不需要把选课码放出来
    private Long courseId;

    private String courseName;

    private List<Long> teacherIds; // 序列化好的字符串

    private List<String> courseImages; // 序列化好的字符串

    private String targetGrade;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private String description;
}
