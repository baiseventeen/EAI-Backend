package com.njuzr.eaibackend.dto;

import com.njuzr.eaibackend.enums.AssignmentCompletionStatus;
import lombok.Data;
// TODO:(柏琪）
/**
 * @author 柏琪
 * @date 2024-07-27 16:23
 */
@Data
public class EngagementQueryDTO {
    private Long assignmentId;
    private AssignmentCompletionStatus status;
}
