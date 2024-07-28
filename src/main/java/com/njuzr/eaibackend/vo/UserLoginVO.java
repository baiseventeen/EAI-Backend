package com.njuzr.eaibackend.vo;

import com.njuzr.eaibackend.po.base.BaseUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/20 - 15:36
 * @Package: EAI-Backend
 */

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLoginVO extends BaseUser {
    private Long id;
    private String token;
}
