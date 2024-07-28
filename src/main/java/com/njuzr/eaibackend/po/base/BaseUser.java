package com.njuzr.eaibackend.po.base;

import com.njuzr.eaibackend.enums.Gender;
import com.njuzr.eaibackend.enums.Role;
import lombok.Data;

import java.util.Date;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/15 - 09:49
 * @Package: EAI-Backend
 * 基础用户类，是所有User相关类的父类
 */

@Data
public class BaseUser {
    private String name; // 真实姓名（必填）

    private String officialEmail; // 南大官邮，xxx@smail.nju.edu.cn（必填）

    private String officialNumber; // 南大学号、工号（必填）

    private Role role; // 用户角色：学生、教师、管理员（必填）

    private String userAvatar; // 头像URL（有默认值、选填）

    private Date createTime; // 创建时间，系统自动设置（自动生成）

    private Date lastLoginTime; // 上一次登陆时间（可选）

    private String englishName; // 英文名（选填）

    private Gender gender; // 性别（选填）

    private Date birthday; // 生日（选填）

    private String phone; // 联系方式（选填）

    private String contentEmail; // 联系邮箱（选填）
}
