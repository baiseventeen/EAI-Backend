package com.njuzr.eaibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njuzr.eaibackend.po.Translation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/21 - 01:50
 * @Package: EAI-Backend
 */

@Mapper
public interface TranslationMapper extends BaseMapper<Translation> {
    List<Translation> findTranslationsByUserIdAndAssignmentId(Long assignmentId, Long userId);
}
