package com.njuzr.eaibackend.po;

import com.njuzr.eaibackend.enums.Role;
import com.njuzr.eaibackend.po.base.BaseUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/20 - 16:30
 * @Package: EAI-Backend
 */

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class MyUserDetails extends BaseUser implements UserDetails {
    private Long id; // 将User所有信息都包含进去（方便在Controller中的操作）
    private String username;
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 用户权限
        List<GrantedAuthority> authorities = new ArrayList<>();
        Role role = getRole();
        authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRole())); // 需要authorities列表之前加上ROLE_前缀
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        // 在User相关类中没有username字段，此处的username其实就是学号
        return getOfficialNumber();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
