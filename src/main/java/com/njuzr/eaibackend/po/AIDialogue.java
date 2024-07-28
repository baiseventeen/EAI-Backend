package com.njuzr.eaibackend.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/3 - 10:33
 * @Package: EAI-Backend
 */

@Data
@Document(collection = "aiDialogues")
public class AIDialogue {
    @Id
    private String id;

    @Indexed
    private Long assignmentId; // 外键

    @Indexed
    private Long userId; // 外键，需要建立(assignmentId, userId)外键索引

    private List<DialogueEntry> dialogues;


    @Data
    @AllArgsConstructor
    public static class DialogueEntry {
        private String role;
        private String content;
        private Long timestamp;
    }
}
