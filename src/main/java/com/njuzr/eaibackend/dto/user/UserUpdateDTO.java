package com.njuzr.eaibackend.dto.user;

import com.njuzr.eaibackend.enums.Gender;
import lombok.Data;

import java.util.Date;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/24 - 10:21
 * @Package: EAI-Backend
 */

@Data
public class UserUpdateDTO {
    private String userAvatar; // 头像URL（有默认值、选填）

    private Date lastLoginTime; // 上一次登陆时间（可选）

    private String englishName; // 英文名（选填）

    private Gender gender; // 性别（选填）

    private Date birthday; // 生日（选填）

    private String phone; // 联系方式（选填）

    private String contentEmail; // 联系邮箱（选填）
}
