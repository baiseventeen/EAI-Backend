package com.njuzr.eaibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.controller.MyResponse;
import com.njuzr.eaibackend.dto.course.CourseDTO;
import com.njuzr.eaibackend.dto.course.CourseQueryDTO;
import com.njuzr.eaibackend.dto.course.CourseUpdateDTO;
import com.njuzr.eaibackend.dto.course.EnrollDTO;
import com.njuzr.eaibackend.enums.Role;
import com.njuzr.eaibackend.exception.MyException;
import com.njuzr.eaibackend.mapper.CourseMapper;
import com.njuzr.eaibackend.mapper.CourseStudentMapper;
import com.njuzr.eaibackend.mapper.UserMapper;
import com.njuzr.eaibackend.po.Course;
import com.njuzr.eaibackend.po.Enrollment;
import com.njuzr.eaibackend.po.MyUserDetails;
import com.njuzr.eaibackend.po.User;
import com.njuzr.eaibackend.service.CourseService;
import com.njuzr.eaibackend.utils.ConvertUtil;
import com.njuzr.eaibackend.utils.ModelMapperUtil;
import com.njuzr.eaibackend.utils.PageMapperUtil;
import com.njuzr.eaibackend.vo.CourseVO;
import com.njuzr.eaibackend.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/5 - 22:09
 * @Package: EAI-Backend
 */

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;

    private final UserMapper userMapper;

    private final CourseStudentMapper courseStudentMapper;

    @Autowired
    public CourseServiceImpl(CourseMapper courseMapper, UserMapper userMapper, CourseStudentMapper courseStudentMapper) {
        this.courseMapper = courseMapper;
        this.userMapper = userMapper;
        this.courseStudentMapper = courseStudentMapper;
    }


    @Override
    public CourseVO createCourse(CourseDTO courseDTO) {
        // 类型转换
        Course targetCourse = convertToPO(courseDTO);
        targetCourse.setCreateTime(new Date());

        log.info("类型转换后的targetCourse"+targetCourse);

        // 插入数据库
        int status = courseMapper.insert(targetCourse);
        if (status == 0) {
            log.error("创建课程失败，数据库插入错误！");
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"创建课程错误");
        }

        return convertToVO(targetCourse);
    }


    @Override
    public IPage<CourseVO> findCoursesPage(Page<Course> page, CourseQueryDTO courseQueryDTO) {
        log.info(courseQueryDTO.toString());
        QueryWrapper<Course> wrapper = getCourseQueryWrapper(courseQueryDTO);

        IPage<Course> courses = courseMapper.selectPage(page, wrapper);

        return PageMapperUtil.convert(courses, this::convertToVO);
    }

    /**
     * 设置查询条件
     * @param courseQueryDTO
     * @return
     */
    private static QueryWrapper<Course> getCourseQueryWrapper(CourseQueryDTO courseQueryDTO) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();

        if (courseQueryDTO.getCourseId() != null) { // courseId直接判断是否相等
            wrapper.eq("course_id", courseQueryDTO.getCourseId());
        }
        if (courseQueryDTO.getTeacherId() != null) { // 模糊匹配teacherIds中是否包含这个字段
            wrapper.like("teacher_ids", "%" + courseQueryDTO.getTeacherId() + "%"); // 模糊匹配
        }
        if (courseQueryDTO.getCourseName() != null) { // 模糊匹配课程名字
            wrapper.like("course_name", courseQueryDTO.getCourseName());
        }

        if (courseQueryDTO.getStudentId() != null) { // 联表查询学生所选课程
            wrapper.inSql("course_id", "SELECT course_id FROM course_student WHERE student_id = " + courseQueryDTO.getStudentId());
        }

        if (courseQueryDTO.getSemester() != null) { // 课程时间过滤，转换成具体日期
            String semester = courseQueryDTO.getSemester();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate startDate, endDate;
            // 解析输入字符串获取学年和学期
            try {
                String[] parts = semester.split("学年第");
                String[] years = parts[0].split("-");
                int yearStart = Integer.parseInt(years[0]);
                int semesterType = parts[1].startsWith("一") ? 1 : 2;

                // 根据学期计算开始和结束日期
                if (semesterType == 1) {
                    startDate = LocalDate.of(yearStart, 9, 1);
                    endDate = LocalDate.of(yearStart + 1, 3, 1);
                } else {
                    startDate = LocalDate.of(yearStart + 1, 3, 1);
                    endDate = LocalDate.of(yearStart + 1, 9, 1);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw MyException.create(HttpStatus.BAD_REQUEST, "学期字符串解析错误");
            }

            wrapper.ge("start_time", startDate.format(formatter))
                    .le("end_time", endDate.format(formatter));
        }

        return wrapper;
    }

    /**
     * 更新课程信息
     * @param courseUpdateDTO
     */
    @Override
    public CourseVO updateCourse(Long userId, Long courseId, CourseUpdateDTO courseUpdateDTO) {
        User targetUser = userMapper.selectById(userId);
        Course targetCourse = courseMapper.selectById(courseId);

        if (targetCourse == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "课程不存在");

        if (targetUser.getRole() != Role.ADMIN && !targetCourse.getTeacherIds().contains(String.valueOf(userId)))
            throw MyException.create(HttpStatus.BAD_REQUEST, "无权限更新");

        Course opeCourse = convertToPO(courseUpdateDTO);
        opeCourse.setCourseId(courseId);

        try {
            int code = courseMapper.updateById(opeCourse);
            if (code == 0) {
                throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "更新课程失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "更新课程失败");
        }

        return convertToVO(courseMapper.selectById(courseId));
    }


    /**
     * 删除课程
     * @param user
     * @param courseId
     */
    @Override
    public void deleteById(MyUserDetails user, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "课程不存在");

        if (user.getRole() != Role.ADMIN && !course.getTeacherIds().contains(String.valueOf(user.getId())))
            throw MyException.create(HttpStatus.BAD_REQUEST, "无权限删除");

        try {
            int code = courseMapper.deleteById(courseId);
            if (code == 0)
                throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "删除课程失败");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "删除课程失败");
        }
    }

    /**
     * 学生选课。给course_student表添加记录。
     * @param enrollDTO
     */
    @Override
    public void enroll(EnrollDTO enrollDTO) {
        Course target = courseMapper.selectById(enrollDTO.getCourseId());

        if (target == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "课程不存在");
        if (!target.getEnrollCode().equals(enrollDTO.getEnrollCode()))
            throw MyException.create(HttpStatus.BAD_REQUEST, "选课码错误");

        Enrollment enrollment = ModelMapperUtil.map(enrollDTO, Enrollment.class);
        courseStudentMapper.insert(enrollment);
    }


    /**
     * 获取某课程的所有选课学生信息
     * @param page
     * @param teacherId
     * @param courseId
     * @return
     */
    @Override
    public IPage<UserVO> getEnrollmentsByCourse(Page<User> page, Long teacherId, Long courseId) {
        Course target = courseMapper.selectById(courseId);
        if (target == null) throw MyException.create(HttpStatus.BAD_REQUEST, "课程不存在");
        if (!target.getTeacherIds().contains(String.valueOf(teacherId)))
            throw MyException.create(HttpStatus.BAD_REQUEST, "无权限查看");

        Page<User> students = courseStudentMapper.selectStudentsByCourseId(page, courseId);

        return PageMapperUtil.convert(students, student -> ModelMapperUtil.map(student, UserVO.class));
    }


    @Override
    public IPage<CourseVO> getEnrollmentsByStudent(Page<Course> page, Long studentId) {
        User student = userMapper.selectById(studentId);
        if (student == null) throw MyException.create(HttpStatus.BAD_REQUEST, "用户不存在");
        Page<Course> courses = courseStudentMapper.selectCoursesByStudentId(page, studentId);
        return PageMapperUtil.convert(courses, this::convertToVO);
    }

    /**
     * 将DTO转换成PO
     * @param courseDTO
     * @return
     */
    private Course convertToPO(CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setTeacherIds(ConvertUtil.listToString(courseDTO.getTeacherIds()));
        if (courseDTO.getCourseImages() != null)
            course.setCourseImages(ConvertUtil.listToString(courseDTO.getCourseImages()));
        return course;
    }

    private Course convertToPO(CourseUpdateDTO updateDTO) {
        Course course = new Course();
        BeanUtils.copyProperties(updateDTO, course);
        if (updateDTO.getTeacherIds() != null)
            course.setTeacherIds(ConvertUtil.listToString(updateDTO.getTeacherIds()));
        if (updateDTO.getCourseImages() != null)
            course.setCourseImages(ConvertUtil.listToString(updateDTO.getCourseImages()));
        return course;
    }

    /**
     * 将PO转换成VO
     * @param course
     * @return
     */
    private CourseVO convertToVO(Course course) {
        CourseVO res = new CourseVO();
        BeanUtils.copyProperties(course, res);
        res.setTeacherIds(ConvertUtil.stringToList(course.getTeacherIds(), Long::valueOf));
        if (course.getCourseImages() != null)
            res.setCourseImages(ConvertUtil.stringToList(course.getCourseImages(), String::valueOf));

        return res;
    }

}
