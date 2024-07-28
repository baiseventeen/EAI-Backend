package com.njuzr.eaibackend.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/5 - 19:58
 * @Package: EAI-Backend
 */

@AllArgsConstructor
@Data
public class EnrollDTO {
    private Long studentId;
    private Long courseId;
    private String enrollCode;
}
