package com.njuzr.eaibackend.dto.user;

import com.njuzr.eaibackend.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/23 - 14:36
 * @Package: EAI-Backend
 */

@Data
public class AdminRegisterDTO {
    @NotNull(message = "name不能为空")
    private String name; // 真实姓名（必填）

    @NotNull(message = "officialEmail不能为空")
    private String officialEmail; // 南大官邮，xxx@smail.nju.edu.cn（必填）

    @NotNull(message = "officialNumber不能为空")
    private String officialNumber; // 南大学号、工号（必填）

    @NotNull(message = "role不能为空")
    private Role role; // 用户角色：学生、教师、管理员（必填）
}
