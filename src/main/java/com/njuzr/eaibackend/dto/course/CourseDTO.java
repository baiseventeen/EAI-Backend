package com.njuzr.eaibackend.dto.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/5 - 16:57
 * @Package: EAI-Backend
 */

@Data
public class CourseDTO {
    @NotNull(message = "课程名不能缺失")
    private String courseName;

    @NotNull(message = "老师Id不能缺失")
    private List<Long> teacherIds;

    private List<String> courseImages;

    private String targetGrade;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "起始时间不能缺失")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "截止时间不能缺失")
    private Date endTime;

    @NotNull(message = "课程描述不能缺失")
    private String description;

    @NotNull(message = "选课码不能缺失")
    private String enrollCode; // 选课码
}
