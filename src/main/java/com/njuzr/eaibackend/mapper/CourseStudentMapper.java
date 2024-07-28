package com.njuzr.eaibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.po.Course;
import com.njuzr.eaibackend.po.Enrollment;
import com.njuzr.eaibackend.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/11 - 21:09
 * @Package: EAI-Backend
 */

@Mapper
public interface CourseStudentMapper extends BaseMapper<Enrollment> {
    Page<User> selectStudentsByCourseId(Page<User> page, Long courseId);

    Page<Course> selectCoursesByStudentId(Page<Course> page, Long studentId);
}
