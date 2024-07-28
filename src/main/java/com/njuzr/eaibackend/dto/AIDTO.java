package com.njuzr.eaibackend.dto;

import com.njuzr.eaibackend.po.AIEntry;
import lombok.Data;

import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/2 - 00:28
 * @Package: EAI-Backend
 */

@Data
public class AIDTO {
    private String model; // 设置AI类别，目前支持ChatGLM3、Qwen
    private List<AIEntry> messages;
}
