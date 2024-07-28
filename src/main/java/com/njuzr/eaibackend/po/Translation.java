package com.njuzr.eaibackend.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/21 - 01:45
 * @Package: EAI-Backend
 */

@Data
@TableName("translations")
public class Translation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long assignmentId;

    private String queryText;

    private String queryResult;

    private String targetLanguage; // 目标语言类型

    private Date queryTime;
}
