package com.njuzr.eaibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njuzr.eaibackend.po.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/5 - 16:31
 * @Package: EAI-Backend
 */

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
