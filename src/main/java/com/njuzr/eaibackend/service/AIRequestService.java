package com.njuzr.eaibackend.service;

import com.njuzr.eaibackend.exception.MyException;
import com.njuzr.eaibackend.po.AIEntry;
import com.njuzr.eaibackend.utils.WebClientUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/2 - 00:00
 * @Package: EAI-Backend
 */

@Slf4j
@Service
public class AIRequestService {

    @Value("${openai.chatgpt.model}")
    private String model;

    @Value("${openai.chatgpt.api.key}")
    private String key;

    private final WebClientUtil chatgptClient;
    private final WebClientUtil chatglmClient;
    private final WebClientUtil qwenClient;

    private final String prefix = "/v1/chat/completions";

    public AIRequestService(WebClientUtil chatgptClient, WebClientUtil chatglmClient, WebClientUtil qwenClient) {
        this.chatgptClient = chatgptClient;
        this.chatglmClient = chatglmClient;
        this.qwenClient = qwenClient;
    }

    public AIResponse requestChatGPT(List<AIEntry> messages) {
        MyRequestObject requestObject = new MyRequestObject(model, messages);
        String chatgptUrl = "https://api.openai.com";
        try {
            return chatgptClient.postWithToken(chatgptUrl+prefix, requestObject, AIResponse.class, key);
        } catch (Exception e) {
            log.error("WebClient请求失败，AI请求失败~");
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"服务器请求AI出错");
        }
    }


    public AIResponse requestChatGLM(List<AIEntry> messages) {
        MyRequestObject requestObject = new MyRequestObject("ChatGLM3", messages);
        String chatglmUrl = "http://10.58.0.2:6678";

        try {
            return chatglmClient.post(chatglmUrl+prefix, requestObject, AIResponse.class);
        } catch (Exception e) {
            log.error("WebClient请求失败，AI请求失败~");
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"服务器请求AI出错");
        }
    }

    public AIResponse requestQWen(List<AIEntry> messages) {
        MyRequestObject requestObject = new MyRequestObject("QWen14B", messages);
        String qwenUrl = "http://10.58.0.2:6679";

        try {
            return qwenClient.post(qwenUrl+prefix, requestObject, AIResponse.class);
        } catch (Exception e) {
            log.error("WebClient请求失败，AI请求失败~");
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"服务器请求AI出错");
        }
    }

    @Data
    @AllArgsConstructor
    static class MyRequestObject {
        private String model;
        private List<AIEntry> messages;
    }

    @Data
    public static class AIResponse {
        private String id;
        private List<Choice> choices;
        private Long created;
        private String model;
        private String object;
        private String system_fingerprint;
        private Usage usage;
    }

    @Data
    static class Choice {
        private String finish_reason;
        private int index;
        private String logprobs;
        private ResponseMessage message;
    }

    @Data
    static class ResponseMessage {
        private String content; // AI生成的回复
        private String role;
        private String function_call;
        private String tool_calls;
    }

    @Data
    static class Usage {
        private int completion_tokens;
        private int prompt_tokens;
        private int total_tokens;
    }
}
