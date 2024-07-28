package com.njuzr.eaibackend.dto;

import com.njuzr.eaibackend.enums.AssignmentStatus;
import lombok.Data;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/16 - 12:08
 * @Package: EAI-Backend
 */

@Data
public class AssignmentQueryDTO {
    private Long assignmentId;

    private Long teacherId;

    private Long courseId;

    private String assignmentName;

    private AssignmentStatus status;
}
