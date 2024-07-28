package com.njuzr.eaibackend.dto;

import lombok.Data;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/21 - 01:13
 * @Package: EAI-Backend
 */

@Data
public class TranslationDTO {
    private String q;
    private String from;
    private String to;
}
