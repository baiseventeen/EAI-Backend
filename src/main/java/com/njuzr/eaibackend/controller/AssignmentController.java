package com.njuzr.eaibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.dto.AssignmentDTO;
import com.njuzr.eaibackend.dto.AssignmentQueryDTO;
import com.njuzr.eaibackend.dto.AssignmentUpdateDTO;
import com.njuzr.eaibackend.dto.CorrectDTO;
import com.njuzr.eaibackend.enums.AssignmentCompletionStatus;
import com.njuzr.eaibackend.po.Assignment;
import com.njuzr.eaibackend.po.Engagement;
import com.njuzr.eaibackend.po.MyUserDetails;
import com.njuzr.eaibackend.service.AIDialogueService;
import com.njuzr.eaibackend.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/5 - 20:01
 * @Package: EAI-Backend
 */

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;

    private final AIDialogueService aiDialogueService;


    @Autowired
    public AssignmentController(AssignmentService assignmentService, AIDialogueService aiDialogueService) {
        this.assignmentService = assignmentService;
        this.aiDialogueService = aiDialogueService;
    }

    /**
     * 创建作业，在某一个课程下创建作业
     * @param courseId
     * @param assignmentDTO
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    public MyResponse createAssignment(
            @AuthenticationPrincipal MyUserDetails user,
            @RequestParam Long courseId,
            @Validated @RequestBody AssignmentDTO assignmentDTO
    ) {
        return MyResponse.success(assignmentService.createAssignment(user, courseId, assignmentDTO));
    }


    // TODO:Get改Post
    @PostMapping("/query")
    public MyResponse findAssignmentPage(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestBody AssignmentQueryDTO assignmentQueryDTO
    ) {
        Page<Assignment> page = new Page<>(currentPage, pageSize);
        return MyResponse.success(assignmentService.findAssignmentPage(page, assignmentQueryDTO));
    }



    @GetMapping("/byCourse/{courseId}")
    public MyResponse getAssignmentsByCourseId(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @PathVariable Long courseId
    ) {
        Page<Assignment> page = new Page<>(currentPage, pageSize);
        AssignmentQueryDTO assignmentQueryDTO = new AssignmentQueryDTO();
        assignmentQueryDTO.setCourseId(courseId);
        return MyResponse.success(assignmentService.findAssignmentPage(page, assignmentQueryDTO));
    }

    @GetMapping("/byAssignment/{assignmentId}")
    public MyResponse getAssignmentById(
            @PathVariable Long assignmentId
    ) {
        return MyResponse.success(assignmentService.findAssignmentById(assignmentId));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping
    public MyResponse updateAssignment(
            @AuthenticationPrincipal MyUserDetails user,
            @RequestParam Long assignmentId,
            @RequestBody AssignmentUpdateDTO assignmentDTO
    ) {
        return MyResponse.success(assignmentService.updateAssignment(user, assignmentId, assignmentDTO));
    }

    /**
     * 根据id删除某个作业
     * @param teacherId
     * @param assignmentId
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping
    public MyResponse deleteAssignment(
            @AuthenticationPrincipal(expression = "id") Long teacherId,
            @RequestParam Long assignmentId
    ) {
        assignmentService.deleteById(teacherId, assignmentId);
        return MyResponse.success("删除作业成功");
    }


    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/engage")
    public MyResponse engageAssignment(
            @AuthenticationPrincipal(expression = "id") Long studentId,
            @RequestParam Long assignmentId
    ) {
        assignmentService.engageAssignment(studentId, assignmentId);
        aiDialogueService.createAIDialogue(assignmentId, studentId);
        return MyResponse.success("参加作业成功");
    }


    @GetMapping("/engage")
    public MyResponse getEngagement(
            @RequestParam Long studentId,
            @RequestParam Long assignmentId
    ) {
        return MyResponse.success(assignmentService.getEngagement(studentId, assignmentId));
    }

    // TODO: (柏琪) 新增接口
    @PostMapping("/engage/query")
    public MyResponse findEngagementPage(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam Long assignmentId

    ) {
        Page<Engagement> page = new Page<>(currentPage, pageSize);
        return MyResponse.success(assignmentService.findEngagementPage(page, assignmentId));
    }

    @GetMapping("/engage/detail")
    public MyResponse getDetailEngagement(
            @RequestParam Long studentId,
            @RequestParam Long assignmentId
    ) {
        return MyResponse.success(assignmentService.getDetailEngagement(studentId, assignmentId));
    }

    @PutMapping("/engage/submit")
    public MyResponse engagementSubmit(
            @RequestParam Long studentId,
            @RequestParam Long assignmentId

    ) {
        assignmentService.engagementSubmit(studentId, assignmentId);
        return MyResponse.success("作业提交成功");
    }


    @PutMapping("/engage/stage")
    public MyResponse engagementStage(
            @RequestParam Long studentId,
            @RequestParam Long assignmentId
    ) {
        assignmentService.engagementStage(studentId, assignmentId);
        return MyResponse.success("作业暂存成功");
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/engage/correct")
    public MyResponse engagementCorrect(
            @AuthenticationPrincipal(expression = "id") Long teacherId,
            @RequestParam Long studentId,
            @RequestParam Long assignmentId,
            @RequestBody CorrectDTO correctDTO
    ) {
        assignmentService.engagementCorrect(teacherId, studentId, assignmentId, correctDTO);
        return MyResponse.success("批改成功");
    }


    /**
     * 更新状态（需要多种情况讨论）
     * @param assignmentId
     * @param status
     * @return
     */
    @PutMapping("/updateStatus")
    public MyResponse updateStatus(
            @RequestParam Long assignmentId,
            @RequestParam AssignmentCompletionStatus status) {
        return MyResponse.success("");
    }

}
