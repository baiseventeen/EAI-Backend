package com.njuzr.eaibackend.dto.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/12 - 11:12
 * @Package: EAI-Backend
 */

@Data
public class CourseUpdateDTO {
    private String courseName;

    private List<Long> teacherIds;

    private List<String> courseImages;

    private String targetGrade;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String description;

    private String enrollCode; // 选课码
}
