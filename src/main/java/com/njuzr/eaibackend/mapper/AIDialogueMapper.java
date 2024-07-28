package com.njuzr.eaibackend.mapper;

import com.njuzr.eaibackend.po.AIDialogue;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/3 - 11:06
 * @Package: EAI-Backend
 */

public interface AIDialogueMapper extends MongoRepository<AIDialogue, String> {
    Page<AIDialogue> findByAssignmentIdAndUserId(Long assignmentId, Long userId, Pageable pageable);

}
