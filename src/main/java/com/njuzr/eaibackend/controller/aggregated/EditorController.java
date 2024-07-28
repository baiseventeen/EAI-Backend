package com.njuzr.eaibackend.controller.aggregated;

import com.njuzr.eaibackend.controller.MyResponse;
import com.njuzr.eaibackend.service.AIDialogueService;
import com.njuzr.eaibackend.service.AssignmentService;
import com.njuzr.eaibackend.vo.AIDialogueVO;
import com.njuzr.eaibackend.vo.AssignmentVO;
import com.njuzr.eaibackend.vo.EngagementVO;
import com.njuzr.eaibackend.vo.aggregated.EditorDataVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/19 - 19:57
 * @Package: EAI-Backend
 */

@RestController
@RequestMapping("/api/aggregated/editor")
public class EditorController {
    private final AssignmentService assignmentService;

    private final AIDialogueService aiDialogueService;

    public EditorController(AssignmentService assignmentService, AIDialogueService aiDialogueService) {
        this.assignmentService = assignmentService;
        this.aiDialogueService = aiDialogueService;
    }

    @GetMapping
    public MyResponse getEditorData(
            @RequestParam Long studentId,
            @RequestParam Long assignmentId
    ) {

        // 1 作业的详情信息 getAssignmentById，获取作业的基本信息，重点是作业描述、作业附件、截止时间（剩余时间）
        AssignmentVO assignmentVO = assignmentService.findAssignmentById(assignmentId);

        // 2 学生参与作业的详细信息 getEngagement，获取文件链接和文件key
        EngagementVO  engagementVO = assignmentService.getEngagement(studentId, assignmentId);

        // 3 学生AI对话的历史记录（存储在mongodb中）
        AIDialogueVO aiDialogueVO = aiDialogueService.getAIDialogue(assignmentId, studentId, 0, 10);

        // 4 学生查询字典的历史记录（MySQL中）

        EditorDataVO editorDataVO = new EditorDataVO();

        editorDataVO.setAssignmentVO(assignmentVO);
        editorDataVO.setEngagementVO(engagementVO);
        editorDataVO.setAiDialogueVO(aiDialogueVO);

        return MyResponse.success(editorDataVO);
    }
}
