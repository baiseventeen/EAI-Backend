package com.njuzr.eaibackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/17 - 11:14
 * @Package: EAI-Backend
 */

@Data
public class AssignmentUpdateDTO {
    private String assignmentName;

    private String description;

    private String descriptionFile; // 作业要求，Word/Pdf文件

    private List<String> attachments; // 作业附件

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
