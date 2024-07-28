package com.njuzr.eaibackend.controller;

import com.njuzr.eaibackend.dto.AIDTO;
import com.njuzr.eaibackend.po.AIDialogue;
import com.njuzr.eaibackend.po.AIEntry;
import com.njuzr.eaibackend.service.AIDialogueService;
import com.njuzr.eaibackend.service.AIRequestService;
import com.njuzr.eaibackend.vo.AIDialogueVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/1 - 23:36
 * @Package: EAI-Backend
 */

@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final AIDialogueService aiDialogueService;

    private final AIRequestService aiRequestService;

    @Autowired
    public AIController(AIDialogueService aiDialogueService, AIRequestService aiRequestService) {
        this.aiDialogueService = aiDialogueService;
        this.aiRequestService = aiRequestService;
    }

    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    @GetMapping
    public MyResponse getDialogues(
            @RequestParam Long assignmentId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size

    ) {
        AIDialogueVO dialogues = aiDialogueService.getAIDialogue(assignmentId, userId, page, size);
        return MyResponse.success(dialogues);
    }

    /**
     * 获得AI智能评价的内容（取最后一个即可）
     * @return
     */
    @GetMapping("/rewrite/result")
    public MyResponse getRewriteResult(
            @RequestParam Long assignmentId,
            @RequestParam Long userId
    ) {

        return MyResponse.success(aiDialogueService.getRewriteResult(assignmentId, userId));
    }

    /**
     * 学生进入写作界面触发，创建AI会话
     * @param assignmentId
     * @param userId
     * @return
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping
    public MyResponse createDialogue(
            @RequestParam Long assignmentId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        aiDialogueService.createAIDialogue(assignmentId, userId);
        return MyResponse.success("创建AI会话成功");
    }

    /**
     * 学生请求AI：1WebClient请求，获得结果；2存储+返回
     * @param dialogueId
     * @param aidto
     * @return
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PutMapping
    public MyResponse requestAI(
            @RequestParam String dialogueId,
            @RequestBody AIDTO aidto
            ) {
        log.info("解析参数为{}, AI会话ID为{}", aidto.toString(), dialogueId);
        AIEntry entry = aiDialogueService.requestAI(dialogueId, aidto);
        return MyResponse.success(entry);
    }

    @PostMapping("/rewrite")
    public MyResponse rewrite(
            @RequestParam Long assignmentId,
            @RequestParam Long studentId
    ) {
        AIEntry entry = aiDialogueService.rewrite(assignmentId, studentId);
        return MyResponse.success(entry);
    }

    @PostMapping("/chatgpt")
    public MyResponse requestChatGPT(
            @RequestBody AIDTO aidto
    ) {
        AIRequestService.AIResponse res = aiRequestService.requestChatGPT(aidto.getMessages());
        return MyResponse.success(res);
    }

}
