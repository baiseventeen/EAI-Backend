package com.njuzr.eaibackend.utils;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.njuzr.eaibackend.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/13 - 08:57
 * @Package: EAI-Backend
 */

@Slf4j
@Component
public class OssUtil {
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    private final OSS ossClient;

    @Autowired
    public OssUtil(OSS ossClient) {
        this.ossClient = ossClient;
    }

    // 上传文件
    public String uploadFile(InputStream inputStream, String filePath) {;
        try {
            ossClient.putObject(bucketName, filePath, inputStream);
            // 默认情况下URL有效期是3600秒
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            URL url = ossClient.generatePresignedUrl(bucketName, filePath, expiration);
            return url.toString();
        } catch (Exception e) {
            log.error("服务器上传文件出错"+e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "服务器上传文件出错");
        }
    }

    public String uploadEnduringFile(InputStream inputStream, String filePath) {
        try {
            ossClient.putObject(bucketName, filePath, inputStream);
            return "http://" + bucketName + "." + endpoint + "/" + filePath;
        } catch (Exception e) {
            log.error("服务器上传文件出错" + e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "服务器上传文件出错");
        }
    }


    /**
     * 生成文件的预签名URL以供下载
     * @param filePath 文件在OSS上的路径
     * @param expirationTime 过期时间（单位：毫秒）
     * @return 预签名URL
     */
    public String generatePresignedUrl(String filePath, long expirationTime) {
        // 设置URL过期时间
        Date expiration = new Date(new Date().getTime() + expirationTime);
        // 生成URL
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, filePath, HttpMethod.GET);
        request.setExpiration(expiration);
        URL url = ossClient.generatePresignedUrl(request);
        return url.toString();
    }

    // 列出文件
    public List<String> listFiles() {
        try {
            List<String> fileNames = new ArrayList<>();
            ObjectListing objectListing = ossClient.listObjects(bucketName);
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                fileNames.add(objectSummary.getKey());
            }
            return fileNames;
        } catch (Exception e) {
            log.error("服务器获取文件列表错误："+e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "服务器获取文件列表错误");
        }
    }

    // 删除文件
    public void deleteFile(String filePath) {
        try {
            ossClient.deleteObject(bucketName, filePath);
        } catch (Exception e) {
            log.error("服务器删除文件错误："+e.getMessage());
            throw MyException.create(HttpStatus.INTERNAL_SERVER_ERROR, "服务器删除文件错误");
        }
    }

}
