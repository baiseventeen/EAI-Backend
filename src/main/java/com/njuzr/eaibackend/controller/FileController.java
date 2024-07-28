package com.njuzr.eaibackend.controller;

import com.njuzr.eaibackend.utils.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/13 - 09:27
 * @Package: EAI-Backend
 */

@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController {
    private final OssUtil ossUtil;

    @Autowired
    public FileController(OssUtil ossUtil) {
        this.ossUtil = ossUtil;
    }


    @PostMapping
    public MyResponse upload(@RequestParam("file") MultipartFile file) throws IOException {
        String filePath = "uploads/" + file.getOriginalFilename();
        String url = ossUtil.uploadFile(file.getInputStream(), filePath);
        return MyResponse.success(url);
    }

    @GetMapping
    public MyResponse getFiles() {
        return MyResponse.success(ossUtil.listFiles());
    }

    @DeleteMapping
    public MyResponse delete(@RequestParam String filePath) {
        ossUtil.deleteFile(filePath);
        return MyResponse.success("删除成功");
    }

}
