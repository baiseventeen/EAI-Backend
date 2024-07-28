package com.njuzr.eaibackend.vo;

import com.njuzr.eaibackend.po.AIEntry;
import lombok.Data;
import org.springframework.data.domain.Page;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/3 - 23:23
 * @Package: EAI-Backend
 */

@Data
public class AIDialogueVO {
    private String dialogueId;
    private Page<AIEntry> messages;
}
