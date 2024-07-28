package com.njuzr.eaibackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/2 - 00:30
 * @Package: EAI-Backend
 */

@SpringBootTest
public class AIRequestServiceTest {
    private final AIRequestService aiRequestService;

    @Autowired
    public AIRequestServiceTest(AIRequestService aiRequestService) {
        this.aiRequestService = aiRequestService;
    }

    @Test
    public void testChatGLM() {
    }
}
