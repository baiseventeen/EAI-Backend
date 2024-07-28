package com.njuzr.eaibackend.dto.user;

import com.njuzr.eaibackend.enums.Role;
import com.njuzr.eaibackend.po.base.BaseUser;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * @author: Leonezhurui
 * @Date: 2024/2/15 - 09:14
 * @Package: EAI-Backend
 */

@Data
public class UserRegisterDTO { // 用户注册，前传后
    @NotNull(message = "name不能为空")
    private String name; // 真实姓名（必填）

    @NotNull(message = "password不能为空")
    private String password;  // 密码（加密存储）（必填）

    @NotNull(message = "officialEmail不能为空")
    private String officialEmail; // 南大官邮，xxx@smail.nju.edu.cn（必填）

    @NotNull(message = "验证码不能为空")
    private String verifyCode;

    @NotNull(message = "officialNumber不能为空")
    private String officialNumber; // 南大学号、工号（必填）

    @NotNull(message = "role不能为空")
    private Role role; // 用户角色：学生、教师、管理员（必填）
}
