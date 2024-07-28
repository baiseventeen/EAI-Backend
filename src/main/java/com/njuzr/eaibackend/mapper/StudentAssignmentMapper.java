package com.njuzr.eaibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.po.Engagement;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/15 - 00:46
 * @Package: EAI-Backend
 */

@Mapper
public interface StudentAssignmentMapper extends BaseMapper<Engagement> {
    Engagement findEngagementByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    // TODO：（柏琪）
    Page<Engagement> findEngagementByAssignmentId(Page<Engagement> page, Long assignmentId);
}
