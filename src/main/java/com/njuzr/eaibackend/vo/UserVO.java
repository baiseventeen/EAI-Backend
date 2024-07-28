package com.njuzr.eaibackend.vo;

import com.njuzr.eaibackend.po.base.BaseUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * @author: Leonezhurui
 * @Date: 2024/2/14 - 22:07
 * @Package: EAI-Backend
 */

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class UserVO extends BaseUser{ // 没有password，后传前的安全数据
    private Long id;
}
