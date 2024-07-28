package com.njuzr.eaibackend.controller;

import com.alibaba.fastjson2.JSONObject;
import com.njuzr.eaibackend.utils.FileUtil;
import com.njuzr.eaibackend.utils.OssUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/13 - 21:22
 * @Package: EAI-Backend
 */

@Slf4j
@RestController
@RequestMapping("/api/onlyoffice")
public class OnlyOfficeController {
    private final FileUtil fileUtil = new FileUtil();

    private final OssUtil ossUtil;

    @Autowired
    public OnlyOfficeController(OssUtil ossUtil) {
        this.ossUtil = ossUtil;
    }

    @RequestMapping(path="/callback", method = {RequestMethod.POST, RequestMethod.HEAD})
    public void editCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            // 获取传输的json数据
            Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
            String body = scanner.hasNext() ? scanner.next() : "";
            JSONObject jsonObject = JSONObject.parseObject(body);
            log.info("{}", jsonObject);

            int status = (int) jsonObject.get("status");
            String key = (String) jsonObject.get("key");

            if (status == 6 || status == 2) {
                String fileUrl = (String) jsonObject.get("url");
                String[] fileKeys = key.split("_");

                Resource resource = fileUtil.downloadFile(fileUrl);
                ossUtil.uploadFile(resource.getInputStream(), fileKeys[0]+"_"+fileKeys[1]+".docx");
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            writer.write("{\"error\":-1}");
            return;
        }
        /*
         * status = 1，我们给onlyOffice的服务返回{"error":"0"}的信息。
         * 这样onlyOffice会认为回调接口是没问题的，这样就可以在线编辑文档了，否则的话会弹出窗口说明
         */
        if (Objects.nonNull(writer)) {
            writer.write("{\"error\":0}");
        }
    }


    @Data
    @AllArgsConstructor
    public static class MyRequestObject {
        private String c;
        private String key;
    }

    @Data
    public static class MyResponseObject {
        private String key;
        private int error;
    }


}
