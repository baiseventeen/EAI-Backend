package com.njuzr.eaibackend.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/4 - 23:04
 * @Package: EAI-Backend
 */


@Data
@TableName("course_student")
public class Enrollment {
    @TableId(type = IdType.AUTO)
    private Long enrollmentId;

    @TableField(value = "course_id")
    private Long courseId;

    @TableField(value = "student_id")
    private Long studentId;
}
