package com.njuzr.eaibackend.dto.course;

import lombok.Data;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/12 - 09:14
 * @Package: EAI-Backend
 */

@Data
public class CourseQueryDTO {
    private Long courseId;
    private Long teacherId;
    private Long studentId;
    private String courseName;
    private String semester;
}
