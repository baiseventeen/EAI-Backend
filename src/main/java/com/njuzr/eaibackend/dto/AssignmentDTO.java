package com.njuzr.eaibackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/5 - 20:03
 * @Package: EAI-Backend
 */

@Data
public class AssignmentDTO {
    @NotNull(message = "作业名称不能缺失")
    private String assignmentName;

    @NotNull(message = "作业描述不能缺失")
    private String description;

    private String descriptionFile; // 作业要求，Word/Pdf文件

    private List<String> attachments; // 作业附件

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "起始时间不能缺失")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能缺失")
    private Date endTime;
}
