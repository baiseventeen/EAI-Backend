package com.njuzr.eaibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.dto.AssignmentDTO;
import com.njuzr.eaibackend.dto.AssignmentQueryDTO;
import com.njuzr.eaibackend.dto.AssignmentUpdateDTO;
import com.njuzr.eaibackend.dto.CorrectDTO;
import com.njuzr.eaibackend.enums.AssignmentCompletionStatus;
import com.njuzr.eaibackend.enums.AssignmentStatus;
import com.njuzr.eaibackend.enums.Role;
import com.njuzr.eaibackend.exception.MyException;
import com.njuzr.eaibackend.mapper.*;
import com.njuzr.eaibackend.po.*;
import com.njuzr.eaibackend.service.AssignmentService;
import com.njuzr.eaibackend.utils.*;
import com.njuzr.eaibackend.vo.AssignmentVO;
import com.njuzr.eaibackend.vo.EngagementDetailVO;
import com.njuzr.eaibackend.vo.EngagementVO;
import com.njuzr.eaibackend.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/15 - 00:40
 * @Package: EAI-Backend
 */

@Slf4j
@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentMapper assignmentMapper;
    private final CourseMapper courseMapper;
    private final EngagementMapper engagementMapper;

    private final StudentAssignmentMapper studentAssignmentMapper;

    private final UserMapper userMapper;

    private final OssUtil ossUtil;

    private final FileUtil fileUtil = new FileUtil();

    public AssignmentServiceImpl(AssignmentMapper assignmentMapper, CourseMapper courseMapper, EngagementMapper engagementMapper, StudentAssignmentMapper studentAssignmentMapper, UserMapper userMapper, OssUtil ossUtil) {
        this.assignmentMapper = assignmentMapper;
        this.courseMapper = courseMapper;
        this.engagementMapper = engagementMapper;
        this.studentAssignmentMapper = studentAssignmentMapper;
        this.userMapper = userMapper;
        this.ossUtil = ossUtil;
    }

    @Override
    public AssignmentVO createAssignment(MyUserDetails user, Long courseId, AssignmentDTO assignmentDTO) {
        Course course = courseMapper.selectById(courseId);
        if (course == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "课程不存在");

        if (user.getRole() != Role.ADMIN && !course.getTeacherIds().contains(String.valueOf(user.getId())))
            throw MyException.create(HttpStatus.BAD_REQUEST, "无权限创建");

        Assignment target = convertToPO(assignmentDTO);
        target.setCourseId(courseId);
        target.setTeacherId(user.getId());
        target.setCreateTime(new Date());

        try {
            int code = assignmentMapper.insert(target);
            if (code == 0)
                throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "创建作业失败");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "创建作业失败");
        }
        return convertToVO(target);
    }


    @Override
    public AssignmentVO findAssignmentById(Long assignmentId) {
        QueryWrapper<Assignment> wrapper = new QueryWrapper<>();
        wrapper.eq("assignment_id", assignmentId);
        Assignment res = assignmentMapper.selectOne(wrapper);
        if (res == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "作业不存在");
        return convertToVO(res);
    }

    @Override
    public IPage<AssignmentVO> findAssignmentPage(Page<Assignment> page, AssignmentQueryDTO assignmentQueryDTO) {

        QueryWrapper<Assignment> wrapper = new QueryWrapper<>();

        if (assignmentQueryDTO.getAssignmentId() != null) { // assignmentId直接判断是否相等
            // TODO 检测assignmentId是否正确
            wrapper.eq("assignmentId", assignmentQueryDTO.getAssignmentId());
        }
        if (assignmentQueryDTO.getTeacherId() != null) { // teacherId直接匹配
            wrapper.eq("teacher_id", assignmentQueryDTO.getTeacherId());
        }
        if (assignmentQueryDTO.getCourseId() != null) { // teacherId直接匹配
            wrapper.eq("course_id", assignmentQueryDTO.getCourseId());
        }
        if (assignmentQueryDTO.getAssignmentName() != null) { // 模糊匹配作业名字
            wrapper.like("assignment_name", assignmentQueryDTO.getAssignmentName());
        }
        if (assignmentQueryDTO.getStatus() != null) {
            AssignmentStatus status = assignmentQueryDTO.getStatus();
            if (status == AssignmentStatus.NOT_STARTED) {
                wrapper.gt("start_time", LocalDateTime.now()); // 意思是start_time > now()
            } else if (status == AssignmentStatus.PROCEEDING) {
                wrapper.le("start_time", LocalDateTime.now())
                        .ge("end_time", LocalDateTime.now());
            } else {
                wrapper.lt("end_time", LocalDateTime.now());
            }
        }

        IPage<Assignment> assignments = assignmentMapper.selectPage(page, wrapper);

        return PageMapperUtil.convert(assignments, this::convertToVO);
    }


    @Override
    public AssignmentVO updateAssignment(MyUserDetails user, Long assignmentId, AssignmentUpdateDTO assignmentUpdateDTO) {
        Assignment target = assignmentMapper.selectById(assignmentId);
        if (!Objects.equals(target.getTeacherId(), user.getId()))
            throw MyException.create(HttpStatus.BAD_REQUEST, "无权限更新");

        Assignment ope = convertToPO(assignmentUpdateDTO);
        ope.setAssignmentId(assignmentId);

        try {
            int code = assignmentMapper.updateById(ope);
            if (code == 0)
                throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "服务器更新失败");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "服务器更新失败");
        }

        return convertToVO(assignmentMapper.selectById(assignmentId));
    }


    @Override
    public void deleteById(Long teacherId, Long assignmentId) {
        Assignment target = assignmentMapper.selectById(assignmentId);
        if (!Objects.equals(target.getTeacherId(), teacherId))
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "无权限删除");

        try {
            int code = assignmentMapper.deleteById(assignmentId);
            if (code == 0)
                throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "删除作业错误");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "删除作业错误");
        }

    }


    @Override
    public void engageAssignment(Long studentId, Long assignmentId) {
        // 1创建Engagement实体类，初始化基本信息
        Engagement engagement = new Engagement();
        engagement.setStudentId(studentId);
        engagement.setAssignmentId(assignmentId);
        engagement.setVersion(1); // 设置这是version
        engagement.setStatus(AssignmentCompletionStatus.NOT_SUBMITTED); // 已经进入到页面编辑，但是还没有提交

        // 2通过FileUtil创建一个空的docx文件
        InputStream inputStream = fileUtil.createEmptyDocx();

        String filePath = assignmentId+"_"+studentId+".docx";
        // 3通过OssUtil工具类上传空文件，注意给其命名为`assignmentId/studentId.docx`，并得到文件链接
        String fileName = ossUtil.uploadEnduringFile(inputStream, filePath);
        engagement.setFileUrl(fileName);

        try {
            int code = studentAssignmentMapper.insert(engagement);
            if (code == 0)
                throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "参加作业失败");
        } catch (Exception e) {
            log.error(e.getMessage());
            ossUtil.deleteFile(filePath);
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "参加作业失败");
        }
    }


    /**
     * 根据两个外键，搜索学生参与作业的情况（需要在mysql中设置索引）
     * @param studentId
     * @param assignmentId
     * @return
     */
    @Override
    public EngagementVO getEngagement(Long studentId, Long assignmentId) {
        Engagement engagement = studentAssignmentMapper.findEngagementByStudentIdAndAssignmentId(studentId, assignmentId);
        if (engagement == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "参与作业情况不存在");
        return convertToVO(engagement);
    }

    @Override
    public IPage<EngagementVO> findEngagementPage(Page<Engagement> page, Long assignmentId) {

        IPage<Engagement> engagements = studentAssignmentMapper.findEngagementByAssignmentId(page, assignmentId);

        return PageMapperUtil.convert(engagements, this::convertToVO);
    }


    @Override
    public EngagementDetailVO getDetailEngagement(Long studentId, Long assignmentId) {
        // 1 获取用户详细信息
        User student = userMapper.selectById(studentId);
        if (student == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "学生不存在");

        // 2 获取作业详细信息
        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "作业不存在");

        // 3 获取作业参与详细信息
        Engagement engagement = studentAssignmentMapper.findEngagementByStudentIdAndAssignmentId(studentId, assignmentId);
        if (engagement == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "参与作业情况不存在");


        EngagementDetailVO detailVO = ModelMapperUtil.map(engagement, EngagementDetailVO.class);
        detailVO.setUserVO(ModelMapperUtil.map(student, UserVO.class));
        detailVO.setAssignmentVO(convertToVO(assignment));
        detailVO.setFileKey(assignmentId+"_"+studentId+"_"+engagement.getVersion());

        log.info("请求到详细信息："+detailVO.toString());

        return detailVO;
    }

    /**
     * 作业提交，更新文件版本和作业状态
     * @param studentId
     * @param assignmentId
     */
    @Override
    public void engagementSubmit(Long studentId, Long assignmentId) {
        Engagement engagement = studentAssignmentMapper.findEngagementByStudentIdAndAssignmentId(studentId, assignmentId);
        if (engagement == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "参与作业情况不存在");
        int originalVersion = engagement.getVersion();
        engagement.setVersion(originalVersion+1);
        engagement.setStatus(AssignmentCompletionStatus.SUBMITTED);
        try {
            int code = studentAssignmentMapper.updateById(engagement);
            if (code == 0)
                throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "更新文件版本失败");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "更新文件版本失败");
        }
    }


    /**
     * 作业暂存，更新文件版本，不修改作业状态
     * @param studentId
     * @param assignmentId
     */
    @Override
    public void engagementStage(Long studentId, Long assignmentId) {
        Engagement engagement = studentAssignmentMapper.findEngagementByStudentIdAndAssignmentId(studentId, assignmentId);
        if (engagement == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "参与作业情况不存在");
        int originalVersion = engagement.getVersion();
        engagement.setVersion(originalVersion+1);
        try {
            int code = studentAssignmentMapper.updateById(engagement);
            if (code == 0)
                throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "更新文件版本失败");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "更新文件版本失败");
        }
    }

    @Override
    public void engagementCorrect(Long teacherId, Long studentId, Long assignmentId, CorrectDTO correctDTO) {
        Engagement engagement = studentAssignmentMapper.findEngagementByStudentIdAndAssignmentId(studentId, assignmentId);
        if (engagement == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "参与作业情况不存在");

        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "作业不存在");

        if (!Objects.equals(assignment.getTeacherId(), teacherId))
            throw MyException.create(HttpStatus.BAD_REQUEST, "无权限批改");

        engagement.setStatus(AssignmentCompletionStatus.CORRECTED); // 第一步，修改状态，改为已批改
        engagement.setScore(correctDTO.getScore());
        engagement.setRemark(correctDTO.getRemark());

        try {
            int code = studentAssignmentMapper.updateById(engagement);
            if (code == 0)
                throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "批改作业失败");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "批改作业失败");
        }

    }

    private Assignment convertToPO(AssignmentDTO assignmentDTO) {
        Assignment assignment = new Assignment();
        BeanUtils.copyProperties(assignmentDTO, assignment);
        if (assignmentDTO.getAttachments() != null)
            assignment.setAttachments(ConvertUtil.listToString(assignmentDTO.getAttachments()));
        return assignment;
    }

    private Assignment convertToPO(AssignmentUpdateDTO assignmentDTO) {
        Assignment assignment = new Assignment();
        BeanUtils.copyProperties(assignmentDTO, assignment);
        if (assignmentDTO.getAttachments() != null)
            assignment.setAttachments(ConvertUtil.listToString(assignmentDTO.getAttachments()));
        return assignment;
    }

    private AssignmentVO convertToVO(Assignment assignment) {
        AssignmentVO res = new AssignmentVO();
        BeanUtils.copyProperties(assignment, res);
        if (assignment.getAttachments() != null)
            res.setAttachments(ConvertUtil.stringToList(assignment.getAttachments(), String::valueOf));
        res.setStatus(calculateStatus(res.getStartTime(), res.getEndTime()));
        return res;
    }

    private EngagementVO convertToVO(Engagement engagement) {
        EngagementVO res = ModelMapperUtil.map(engagement, EngagementVO.class);
        res.setFileKey(res.getAssignmentId()+"_"+res.getStudentId()+"_"+res.getVersion());
        return res;
    }

    private AssignmentStatus calculateStatus(Date startTime, Date endTime) {
        Date now = new Date();
        if (now.before(startTime)) {
            return AssignmentStatus.NOT_STARTED;
        } else if (now.after(endTime)) {
            return AssignmentStatus.FINISHED;
        } else {
            return AssignmentStatus.PROCEEDING;
        }
    }
}
