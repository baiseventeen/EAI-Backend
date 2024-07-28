package com.njuzr.eaibackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.dto.AssignmentDTO;
import com.njuzr.eaibackend.dto.AssignmentQueryDTO;
import com.njuzr.eaibackend.dto.AssignmentUpdateDTO;
import com.njuzr.eaibackend.dto.CorrectDTO;
import com.njuzr.eaibackend.po.Assignment;
import com.njuzr.eaibackend.po.Engagement;
import com.njuzr.eaibackend.po.MyUserDetails;
import com.njuzr.eaibackend.vo.AssignmentVO;
import com.njuzr.eaibackend.vo.EngagementDetailVO;
import com.njuzr.eaibackend.vo.EngagementVO;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/15 - 00:40
 * @Package: EAI-Backend
 */

public interface AssignmentService {

    AssignmentVO createAssignment(MyUserDetails user, Long courseId, AssignmentDTO assignmentDTO);

    AssignmentVO findAssignmentById(Long assignmentId);

    IPage<AssignmentVO> findAssignmentPage(Page<Assignment> page, AssignmentQueryDTO assignmentQueryDTO);

    AssignmentVO updateAssignment(MyUserDetails user, Long assignmentId, AssignmentUpdateDTO assignmentUpdateDTO);

    void deleteById(Long teacherId, Long assignmentId);

    void engageAssignment(Long studentId, Long assignmentId);

    EngagementVO getEngagement(Long studentId, Long assignmentId);

    IPage<EngagementVO> findEngagementPage(Page<Engagement> page, Long assignmentId);

    EngagementDetailVO getDetailEngagement(Long studentId, Long assignmentId);

    void engagementSubmit(Long studentId, Long assignmentId);

    void engagementStage(Long studentId, Long assignmentId);

    void engagementCorrect(Long teacherId, Long studentId, Long assignmentId, CorrectDTO correctDTO);
    
}
