package com.njuzr.eaibackend.utils;

import com.njuzr.eaibackend.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/15 - 01:39
 * @Package: EAI-Backend
 */

@Slf4j
public class FileUtil {

    /**
     * 通过文件链接下载文件
     * @param fileUrl
     * @return
     */
    public Resource downloadFile(String fileUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(fileUrl, Resource.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 创建空的docx文件
     * @return
     */
    public ByteArrayInputStream createEmptyDocx() {
        try (XWPFDocument document = new XWPFDocument()) {
            // 这里不添加任何内容，保持docx文件为空；同时，将XWPFDocument转换为byte数组以便上传
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            byte[] docxContent = out.toByteArray();
            return new ByteArrayInputStream(docxContent);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "创建空Docx文件失败");
        }
    }

    /**
     * 从Docx中读取，并转成String
     * @param inputStream
     * @return
     * @throws Exception
     */
    public String readDocxFile(InputStream inputStream) throws Exception {
        XWPFDocument document = new XWPFDocument(inputStream);
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        StringBuilder sb = new StringBuilder();
        for (XWPFParagraph para : paragraphs) {
            sb.append(para.getText()).append("\n");
        }
        return sb.toString();
    }
}
