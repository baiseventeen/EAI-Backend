package com.njuzr.eaibackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.dto.course.CourseDTO;
import com.njuzr.eaibackend.dto.course.CourseQueryDTO;
import com.njuzr.eaibackend.dto.course.CourseUpdateDTO;
import com.njuzr.eaibackend.dto.course.EnrollDTO;
import com.njuzr.eaibackend.po.Course;
import com.njuzr.eaibackend.po.Enrollment;
import com.njuzr.eaibackend.po.MyUserDetails;
import com.njuzr.eaibackend.po.User;
import com.njuzr.eaibackend.vo.CourseVO;
import com.njuzr.eaibackend.vo.UserVO;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/5 - 22:08
 * @Package: EAI-Backend
 */

public interface CourseService {
    CourseVO createCourse(CourseDTO courseDTO);

    IPage<CourseVO> findCoursesPage(Page<Course> page, CourseQueryDTO courseQueryDTO);

    CourseVO updateCourse(Long userId, Long courseId, CourseUpdateDTO courseUpdateDTO);

    void deleteById(MyUserDetails role, Long courseId);

    void enroll(EnrollDTO enrollDTO);

    IPage<UserVO> getEnrollmentsByCourse(Page<User> page, Long teacherId, Long courseId);

    IPage<CourseVO> getEnrollmentsByStudent(Page<Course> page, Long studentId);
}
