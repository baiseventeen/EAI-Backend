package com.njuzr.eaibackend.dto.user;

import lombok.Data;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/20 - 12:40
 * @Package: EAI-Backend
 */

@Data
public class UserLoginDTO { //用户登陆，只需要传递学号和密码
    private String officialNumber;
    private String password;
}
