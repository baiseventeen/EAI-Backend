package com.njuzr.eaibackend.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.njuzr.eaibackend.enums.Gender;
import com.njuzr.eaibackend.enums.Role;
import com.njuzr.eaibackend.po.base.BaseUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/14 - 21:49
 * @Package: EAI-Backend
 */

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true) // 会把父类的属性也输出
@Data
@TableName("users")
public class User extends BaseUser{
    @TableId(type=IdType.AUTO)
    private Long id; // User唯一标识（自增）

    private String password;  // 密码（加密存储）（必填）
}
