package com.njuzr.eaibackend.service;

import com.njuzr.eaibackend.dto.AIDTO;
import com.njuzr.eaibackend.exception.MyException;
import com.njuzr.eaibackend.mapper.AIDialogueMapper;
import com.njuzr.eaibackend.mapper.AIRewriteRecordMapper;
import com.njuzr.eaibackend.mapper.AssignmentMapper;
import com.njuzr.eaibackend.mapper.StudentAssignmentMapper;
import com.njuzr.eaibackend.po.*;
import com.njuzr.eaibackend.utils.FileUtil;
import com.njuzr.eaibackend.utils.ModelMapperUtil;
import com.njuzr.eaibackend.vo.AIDialogueVO;
import com.njuzr.eaibackend.vo.AssignmentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/3 - 11:29
 * @Package: EAI-Backend
 */

@Slf4j
@Service
public class AIDialogueService {
    private final MongoTemplate mongoTemplate;

    private final AIDialogueMapper aiDialogueMapper;

    private final AIRewriteRecordMapper aiRewriteRecordMapper;

    private final AIRequestService aiRequestService;

    private final StudentAssignmentMapper studentAssignmentMapper;

    private final AssignmentMapper assignmentMapper;

    private final FileUtil fileUtil = new FileUtil();

    @Autowired
    public AIDialogueService(MongoTemplate mongoTemplate, AIDialogueMapper aiDialogueMapper, AIRewriteRecordMapper aiRewriteRecordMapper, AIRequestService aiRequestService, StudentAssignmentMapper studentAssignmentMapper, AssignmentMapper assignmentMapper) {
        this.mongoTemplate = mongoTemplate;
        this.aiDialogueMapper = aiDialogueMapper;
        this.aiRewriteRecordMapper = aiRewriteRecordMapper;
        this.aiRequestService = aiRequestService;
        this.studentAssignmentMapper = studentAssignmentMapper;
        this.assignmentMapper = assignmentMapper;
    }

    /**
     * 创建新的AI会话，在学生点击进行写作界面时会自动触发
     * @param assignmentId
     * @param userId
     */
    public void createAIDialogue(Long assignmentId, Long userId) {
        if (aiDialogueMapper.findByAssignmentIdAndUserId(assignmentId, userId, PageRequest.of(0,1)).hasContent()) {
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"AI会话已存在");
        }
        AIDialogue aiDialogue = new AIDialogue();
        aiDialogue.setAssignmentId(assignmentId);
        aiDialogue.setUserId(userId);
        aiDialogue.setDialogues(new ArrayList<>());
        aiDialogueMapper.save(aiDialogue);
    }

    public void createAIRewriteRecords(Long assignmentId, Long userId) {
        if (aiRewriteRecordMapper.findByAssignmentIdAndUserId(assignmentId, userId, PageRequest.of(0,1)).hasContent()) {
            throw MyException.create(HttpStatus.BAD_REQUEST, "AI改写记录已存在");
        }

        AIRewriteRecord aiRewriteRecord = new AIRewriteRecord();
        aiRewriteRecord.setAssignmentId(assignmentId);
        aiRewriteRecord.setUserId(userId);
        aiRewriteRecord.setRecords(new ArrayList<>());

        aiRewriteRecordMapper.save(aiRewriteRecord);
    }



    /**
     * 分页返回查找内容
     * @param assignmentId
     * @param userId
     * @param page
     * @param size
     * @return
     */
    public AIDialogueVO getAIDialogue(Long assignmentId, Long userId, int page, int size) {

        AIDialogue targetDialogue = mongoTemplate.findOne(
                Query.query(Criteria.where("assignmentId").is(assignmentId).and("userId").is(userId)), AIDialogue.class
        );

        if (targetDialogue == null) {
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"找不到AI会话");
        }

        Pageable pageable = PageRequest.of(page, size);

        List<AIDialogue.DialogueEntry> sortedDialogues = targetDialogue.getDialogues();
        sortedDialogues.sort((d1, d2) -> d2.getTimestamp().compareTo(d1.getTimestamp()));

        // 手动处理dialogues数组的分页逻辑
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedDialogues.size());

        if (start > sortedDialogues.size()) {
            start = end = sortedDialogues.size();
        }

        List<AIDialogue.DialogueEntry> pagedDialogues = sortedDialogues.subList(start, end);
        pagedDialogues.sort(Comparator.comparing(AIDialogue.DialogueEntry::getTimestamp));

        List<AIEntry> entries = pagedDialogues.stream().map(entry -> ModelMapperUtil.map(entry, AIEntry.class)).collect(Collectors.toList());

        AIDialogueVO aiDialogueVO = new AIDialogueVO();
        aiDialogueVO.setDialogueId(targetDialogue.getId());

        aiDialogueVO.setMessages(new PageImpl<>(entries, pageable, targetDialogue.getDialogues().size()));

        // 返回包含分页dialogues和总条目数的PageImpl
        return aiDialogueVO;
    }


    public List<AIRewriteRecord.RecordEntry> getRewriteResult(Long assignmentId, Long userId) {
        AIRewriteRecord targetRecord = mongoTemplate.findOne(
                Query.query(Criteria.where("assignmentId").is(assignmentId).and("userId").is(userId)), AIRewriteRecord.class
        );

        if (targetRecord == null) {
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"找不到AI智能批改记录");
        }
        return targetRecord.getRecords();

    }




    public AIEntry requestAI(String dialogueId, AIDTO aidto) {
        // 1 使用WebClient请求数据
        String modelType = aidto.getModel();
        List<AIEntry> entries = aidto.getMessages();
        if (entries.size() > 11) {
            // TODO 限制上下文数量
        }

        // 优先构造userEntry（用于timestamp的构建）
        AIEntry lastEntry = aidto.getMessages().get(aidto.getMessages().size()-1);
        AIDialogue.DialogueEntry userEntry = new AIDialogue.DialogueEntry(lastEntry.getRole(), lastEntry.getContent(), System.currentTimeMillis() / 1000);

        AIRequestService.AIResponse res;
        if (Objects.equals(modelType, "ChatGLM3")) {
            res = aiRequestService.requestChatGLM(aidto.getMessages());
        } else if (modelType.equals("QWen14B")) {
            res = aiRequestService.requestQWen(aidto.getMessages());
        } else if (modelType.equals("ChatGPT")) {
            res = aiRequestService.requestChatGPT(aidto.getMessages());
        } else {
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"模型不支持");
        }

        String retContent;
        String role;
        Long timestamp;
        try {
            retContent = res.getChoices().get(0).getMessage().getContent();
            role = res.getChoices().get(0).getMessage().getRole();
            timestamp = res.getCreated();
        } catch (Exception e) {
            log.error("AI请求返回的数据内容有误，数据为{}", res);
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"请求失败");
        }

        // 2 更新数据库
        AIDialogue.DialogueEntry aiEntry = new AIDialogue.DialogueEntry(role, retContent, timestamp);

        addDialogueEntry(dialogueId, userEntry, aiEntry);

        return new AIEntry(role, retContent);
    }


    public AIEntry rewrite(Long assignmentId, Long studentId) {
        // 1 通过assignmentId和studentId找到engagement，获取当前文件链接
        Engagement engagement = studentAssignmentMapper.findEngagementByStudentIdAndAssignmentId(studentId, assignmentId);
        if(engagement == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "作业参与不存在");

        // 通过assignmentId找到作业，获取作业描述
        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null)
            throw MyException.create(HttpStatus.BAD_REQUEST, "作业不存在");

        String assignmentDescription = assignment.getDescription();

        // 2 下载文件，并转为String类型
        String fileUrl = engagement.getFileUrl();

        String content;
        Resource fileResource = fileUtil.downloadFile(fileUrl);
        try (InputStream inputStream = fileResource.getInputStream()) {
            content = fileUtil.readDocxFile(inputStream);
            log.info("文件内容转换成String为："+content);
        } catch (Exception e) {
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "文件转码出错");
        }

        // 3 构造提示词
        String promptTemplate = "%s\n"
                + "假设你现在是一位英文专业的教师，正在批阅一位同学的写作作业，作业描述是：%s"
                + "请做如下工作：\n"
                + "1. 满分100分，请综合英文用词、句式等方面，给出评分和评分原因\n"
                + "2. 针对文章，给出改进的内容，格式为“xxx”可以修改为“xxx”\n"
                + "3. 给出修改后的完整文章内容\n"
                + "其余的任何描述都不需要，也不需要任何交互！";

        String prompt = String.format(promptTemplate, content, assignmentDescription);
        log.info("内置提示词为："+ prompt);

        // 4 请求AI
        List<AIEntry> entry = new ArrayList<>();
        entry.add(new AIEntry("user", prompt));
        AIRequestService.AIResponse response = aiRequestService.requestChatGLM(entry);

        // 5 获取返回数据
        String retContent;
        String role;
        Long timestamp;
        try {
            retContent = response.getChoices().get(0).getMessage().getContent();
            role = response.getChoices().get(0).getMessage().getRole();
            timestamp = response.getCreated();
            log.info("AI返回的数据为："+retContent);
        } catch (Exception e) {
            log.error("AI请求返回的数据内容有误，数据为{}", response);
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"请求失败");
        }

        // 6 存储数据
        // 如果没有，则创建一条记录
        if (!aiRewriteRecordMapper.findByAssignmentIdAndUserId(assignmentId, studentId, PageRequest.of(0,1)).hasContent()) {
            createAIRewriteRecords(assignmentId, studentId);
        }

        // 找到这条记录
        Page<AIRewriteRecord> records = aiRewriteRecordMapper.findByAssignmentIdAndUserId(assignmentId, studentId, PageRequest.of(0,1));

        // 获取记录的Id
        String recordId = records.getContent().get(0).getId();

        // 更新mongodb数据库
        AIRewriteRecord.RecordEntry userEntry = new AIRewriteRecord.RecordEntry("user", content, System.currentTimeMillis() / 1000);
        AIRewriteRecord.RecordEntry recordEntry = new AIRewriteRecord.RecordEntry(role, retContent, timestamp);
        addRecordEntry(recordId, userEntry, recordEntry);

        return new AIEntry(role, retContent);

    }


    /**
     * 更新MongoDB数据；通过$push的方式，减少更新的开销；请求成功则会一次性更新两条数据，如果不成功，则两条都不插入。
     * @param dialogueId
     * @param userEntry
     * @param aiEntry
     */
    private void addDialogueEntry(String dialogueId, AIDialogue.DialogueEntry userEntry, AIDialogue.DialogueEntry aiEntry) {
        try {
            Query query =  new Query(Criteria.where("id").is(dialogueId));
            Update update = new Update().push("dialogues").each(userEntry, aiEntry);
            mongoTemplate.updateFirst(query, update, AIDialogue.class);
        } catch (Exception e) {
            log.error("MongoDB数据库更新出错");
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"数据库更新失败");
        }
    }

    private void addRecordEntry(String recordId, AIRewriteRecord.RecordEntry userEntry, AIRewriteRecord.RecordEntry aiEntry) {
        try {
            Query query =  new Query(Criteria.where("id").is(recordId));
            Update update = new Update().push("records").each(userEntry, aiEntry);
            mongoTemplate.updateFirst(query, update, AIRewriteRecord.class);
        } catch (Exception e) {
            log.error("MongoDB数据库更新出错");
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"数据库更新失败");
        }
    }
}
