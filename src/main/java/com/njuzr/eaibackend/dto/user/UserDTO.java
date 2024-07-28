package com.njuzr.eaibackend.dto.user;

import com.njuzr.eaibackend.po.base.BaseUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * @author: Leonezhurui
 * @Date: 2024/2/14 - 22:08
 * @Package: EAI-Backend
 */

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class UserDTO extends BaseUser{ // 用户更新，前传后（需要登陆）
}
