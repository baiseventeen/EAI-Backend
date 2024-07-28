package com.njuzr.eaibackend.service;

import com.njuzr.eaibackend.dto.TranslationDTO;
import com.njuzr.eaibackend.exception.MyException;
import com.njuzr.eaibackend.mapper.TranslationMapper;
import com.njuzr.eaibackend.po.Translation;
import com.njuzr.eaibackend.utils.ModelMapperUtil;
import com.njuzr.eaibackend.utils.WebClientUtil;
import com.njuzr.eaibackend.vo.TranslationVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/21 - 01:15
 * @Package: EAI-Backend
 */

@Slf4j
@Service
public class TranslateService {
    private final WebClientUtil webClientUtil;

    private final TranslationMapper translationMapper;

    public TranslateService(WebClientUtil webClientUtil, TranslationMapper translationMapper) {
        this.webClientUtil = webClientUtil;
        this.translationMapper = translationMapper;
    }

    public String translate(Long assignmentId, Long userId, TranslationDTO translationDTO) {
        MyResponseObject response = webClientUtil.post("http://localhost:5000/api/translate", translationDTO, MyResponseObject.class);

        if (Objects.equals(response.getCode(), 200)) {
            Translation translation = new Translation();
            translation.setUserId(userId);
            translation.setAssignmentId(assignmentId);
            translation.setQueryText(translationDTO.getQ());
            translation.setQueryResult(response.data);
            translation.setTargetLanguage(translationDTO.getTo());
            translation.setQueryTime(new Date());

            translationMapper.insert(translation);
        } else {
            log.error("请求字典出错");
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "请求字典出错");
        }

        return response.data;
    }


    @Data
    @AllArgsConstructor
    public static class MyRequestObject {
        private String q;
        private String from;
        private String to;
        private String appKey;
        private String salt;
        private String sign;
        private String signType;
        private String curtime;
    }

    @Data
    public static class MyResponseObject {
        private int code;
        private String data;
        private String msg;
    }


    public List<TranslationVO> getTranslations(Long assignmentId, Long userId) {
        List<Translation> translations = translationMapper.findTranslationsByUserIdAndAssignmentId(assignmentId, userId);
        return translations.stream().map(translation -> ModelMapperUtil.map(translation, TranslationVO.class)).toList();
    }

}
