package com.njuzr.eaibackend.controller;

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
import com.njuzr.eaibackend.service.CourseService;
import com.njuzr.eaibackend.vo.CourseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/5 - 16:34
 * @Package: EAI-Backend
 */

@RestController
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }


    /**
     * 创建课程
     * @param courseDTO
     * @return
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping
    public MyResponse createCourse(@Validated @RequestBody CourseDTO courseDTO) {
        CourseVO courseVO = courseService.createCourse(courseDTO);
        return MyResponse.success(courseVO);
    }


    /**
     * 管理查询课程，内容查询
     * @param currentPage
     * @param pageSize
     * @param courseQueryDTO
     * @return
     */
    // TODO:权限修改
    // @PreAuthorize("hasRole('ADMIN')")
    // TODO: Get改为Post，因为有@RequestBody
    @PostMapping("/query")
    public MyResponse findCoursePage(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestBody CourseQueryDTO courseQueryDTO
            ) {
        Page<Course> page = new Page<>(currentPage, pageSize);
        IPage<CourseVO> courses =  courseService.findCoursesPage(page, courseQueryDTO);

        return MyResponse.success(courses);
    }


    /**
     * 根据学生Id获取其所选的所有课程的信息
     * @param studentId
     * @return
     */
    @GetMapping("/byStudent/{studentId}")
    public MyResponse findCourseByStudentId(
            @PathVariable Long studentId,
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        Page<Course> page = new Page<>(currentPage, pageSize);

        CourseQueryDTO courseQueryDTO = new CourseQueryDTO();
        courseQueryDTO.setStudentId(studentId);

        return MyResponse.success(courseService.findCoursesPage(page, courseQueryDTO));
    }

    /**
     * 根据老师Id获取的其所创建的所有课程信息
     * @param teacherId
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/byTeacher/{teacherId}")
    public MyResponse findByTeacherId(
            @PathVariable Long teacherId,
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        Page<Course> page = new Page<>(currentPage, pageSize);

        CourseQueryDTO courseQueryDTO = new CourseQueryDTO();
        courseQueryDTO.setTeacherId(teacherId);

        return MyResponse.success(courseService.findCoursesPage(page, courseQueryDTO));
    }

    /**
     * 根据课程Id，获取某个课程的详细信息
     * @param courseId
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/{courseId}")
    public MyResponse findByCourseId(
            @PathVariable Long courseId,
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        Page<Course> page = new Page<>(currentPage, pageSize);

        CourseQueryDTO courseQueryDTO = new CourseQueryDTO();
        courseQueryDTO.setCourseId(courseId);

        return MyResponse.success(courseService.findCoursesPage(page, courseQueryDTO));
    }

    @GetMapping("/byName")
    public MyResponse findByCourseName(
            @RequestParam String courseName,
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        Page<Course> page = new Page<>(currentPage, pageSize);

        CourseQueryDTO courseQueryDTO =  new CourseQueryDTO();
        courseQueryDTO.setCourseName(courseName);

        return MyResponse.success(courseService.findCoursesPage(page, courseQueryDTO));
    }


    @GetMapping("/byTime")
    public MyResponse findByCourseTime(
            @RequestParam String courseTime,
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        Page<Course> page = new Page<>(currentPage, pageSize);

        CourseQueryDTO courseQueryDTO =  new CourseQueryDTO();
        courseQueryDTO.setSemester(courseTime);

        return MyResponse.success(courseService.findCoursesPage(page, courseQueryDTO));
    }

    /**
     * 只有课程老师或管理员可以修改当前课程的信息
     * @param userId
     * @param courseId
     * @param updateDTO
     * @return
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping
    public MyResponse updateCourse(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam Long courseId,
            @RequestBody CourseUpdateDTO updateDTO
    ) {
        return MyResponse.success(courseService.updateCourse(userId, courseId, updateDTO));
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/enrollCode")
    public MyResponse updateEnrollCode(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam Long courseId,
            @RequestParam String enrollCode
    ) {
        CourseUpdateDTO updateDTO = new CourseUpdateDTO();
        updateDTO.setEnrollCode(enrollCode);
        return MyResponse.success(courseService.updateCourse(userId, courseId, updateDTO));
    }

    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @DeleteMapping
    public MyResponse deleteCourse(
            @AuthenticationPrincipal MyUserDetails user,
            @RequestParam Long courseId
    ) {
        courseService.deleteById(user, courseId);
        return MyResponse.success("删除课程成功");
    }

    /**
     * 选课，通过studentId和courseId，完成选课。其中studentId从Token中获取。
     * @param studentId
     * @param enrollDTO
     * @return
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/enroll")
    public MyResponse enroll(
            @AuthenticationPrincipal(expression = "id") Long studentId,
            @RequestBody EnrollDTO enrollDTO
    ) {
        enrollDTO.setStudentId(studentId);
        courseService.enroll(enrollDTO);
        return MyResponse.success("选课成功");
    }

    @GetMapping("/getEnrollments/byCourse")
    @PreAuthorize("hasRole('TEACHER')")
    public MyResponse getEnrollmentsByCourse(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @AuthenticationPrincipal(expression = "id") Long teacherId,
            @RequestParam Long courseId
    ) {
        Page<User> page = new Page<>(currentPage, pageSize);
        return MyResponse.success(courseService.getEnrollmentsByCourse(page, teacherId, courseId));
    }

    @GetMapping("/getEnrollments/byStudent")
    public MyResponse getEnrollmentsByStudent(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam Long studentId
    ) {
        Page<Course> page = new Page<>(currentPage, pageSize);
        return MyResponse.success(courseService.getEnrollmentsByStudent(page, studentId));
    }


}
