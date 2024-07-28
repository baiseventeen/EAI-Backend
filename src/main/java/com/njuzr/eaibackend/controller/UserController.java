package com.njuzr.eaibackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.dto.user.UserRegisterDTO;
import com.njuzr.eaibackend.dto.user.UserUpdateDTO;
import com.njuzr.eaibackend.enums.Role;
import com.njuzr.eaibackend.exception.MyException;
import com.njuzr.eaibackend.po.User;
import com.njuzr.eaibackend.service.UserService;
import com.njuzr.eaibackend.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/14 - 22:11
 * @Package: EAI-Backend
 */

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * 学生注册账号，
     * @param userRegisterDTO
     * @return
     */
    @PostMapping("/register")
    public MyResponse createUser(@Validated @RequestBody UserRegisterDTO userRegisterDTO) {
        UserVO userVO = userService.createUser(userRegisterDTO);
        return MyResponse.success(userVO);
    }

    @GetMapping("/verifyCode")
    public MyResponse sendVerifyCode(@RequestParam(value = "officialEmail") String officialEmail) {
        userService.sendVerifyCode(officialEmail);
        return MyResponse.success("发送成功");
    }

    /**
     * （学生、老师）更新自己的用户信息，固定信息不可更改，例如真实姓名、邮箱、学号、角色等
     * @param id
     * @param userDTO
     * @return
     */
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    @PutMapping
    public MyResponse updateUser(@AuthenticationPrincipal(expression = "id") Long id, @RequestBody UserUpdateDTO userDTO) {
        log.info("UserDTO解析成功，对象如下："+userDTO);
        UserVO userVO = userService.updateUser(id, userDTO);
        return MyResponse.success(userVO);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    @PutMapping("/updatePassword")
    public MyResponse updatePassword(
            @AuthenticationPrincipal(expression = "id") Long id,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("oldPassword") String oldPassword) {
        try {
            userService.updatePassword(id, newPassword, oldPassword);
            return MyResponse.success("用户密码更新成功");
        } catch (MyException e) {
            return MyResponse.error(e.getErrCode(), e.getMessage());
        }
    }

    /**
     * （管理员或教师）可以查找具体某个学生
     * @param id
     * @return
     */
    // TODO: 学生端也要根据id查询课程老师姓名
    @GetMapping()
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public MyResponse searchUsers(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "officialNumber",required = false) String officialNumber,
            @RequestParam(value = "role",required = false) Role role) {
        Page<User> page = new Page<>(currentPage, pageSize);

        // TODO 可能会有操作记录的功能
        IPage<UserVO> userVO = userService.searchUsers(page, id, name, officialNumber, role);
        return MyResponse.success(userVO);
    }

}
