package com.njuzr.eaibackend.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/23 - 20:25
 * @Package: EAI-Backend
 */

@Data
public class TranslationVO {
    private String queryText;

    private String queryResult;

    private String targetLanguage; // 目标语言类型

    private Date queryTime;
}
