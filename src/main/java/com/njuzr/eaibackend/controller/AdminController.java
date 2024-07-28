package com.njuzr.eaibackend.controller;

import com.njuzr.eaibackend.dto.user.AdminRegisterDTO;
import com.njuzr.eaibackend.dto.user.UserDTO;
import com.njuzr.eaibackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/23 - 14:22
 * @Package: EAI-Backend
 */

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 管理员创建用户（学生和老师）
     * @param adminRegisterDTO
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/createUser")
    public MyResponse createUser(@Validated @RequestBody AdminRegisterDTO adminRegisterDTO) {
        userService.adminCreateUser(adminRegisterDTO);
        return MyResponse.success("创建成功");
    }

    /**
     * 管理更新用户信息，除id之外都可以修改
     * @param userDTO
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/updateUser")
    public MyResponse updateUser(@RequestParam("id") Long id, @RequestBody UserDTO userDTO) {
        return MyResponse.success(userService.updateUser(id, userDTO));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/resetPassword")
    public MyResponse resetPassword(@RequestParam("id") Long id) {
        userService.resetPassword(id);
        return MyResponse.success("重置密码成功");
    }


    /**
     * （管理员）通过上传excel/csv文件，批量创建用户，文件必须含有字段信息：姓名、学号、邮箱
     * @return
     */
    @PostMapping("/batch-upload")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public MyResponse batchCreateUsers(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();

        // 根据文件类型进行不同的处理
        try {
            if(fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                userService.batchCreateUsersFromExcel(file);
            } else if (fileName.endsWith(".csv")) {
                userService.batchCreateUsersFromCsv(file);
            }else {
                return MyResponse.error(400, "文件类型不支持");
            }
        } catch (Exception e) {
            log.error("上传文件失败，错误如下："+e.getMessage());
            return MyResponse.error(400, "上传文件失败："+e.getMessage());
        }

        return MyResponse.success("批量创建用户成功");
    }

    /**
     * （管理员）根据ID删除某个特定的用户
     * @param id
     * @return
     */
    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public MyResponse deleteById(@RequestParam Long id) {
        userService.deleteById(id);
        return MyResponse.success("删除成功");
    }
}
