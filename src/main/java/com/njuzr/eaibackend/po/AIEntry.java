package com.njuzr.eaibackend.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/3 - 22:30
 * @Package: EAI-Backend
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIEntry {
    private String role;
    private String content;
}
