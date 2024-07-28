package com.njuzr.eaibackend.mapper;

import com.njuzr.eaibackend.po.AIDialogue;
import com.njuzr.eaibackend.po.AIRewriteRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/20 - 19:37
 * @Package: EAI-Backend
 */

public interface AIRewriteRecordMapper extends MongoRepository<AIRewriteRecord, String> {
    Page<AIRewriteRecord> findByAssignmentIdAndUserId(Long assignmentId, Long userId, Pageable pageable);
}
