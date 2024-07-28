package com.njuzr.eaibackend.service;

import com.njuzr.eaibackend.enums.Role;
import com.njuzr.eaibackend.mapper.UserMapper;
import com.njuzr.eaibackend.po.MyUserDetails;
import com.njuzr.eaibackend.po.User;
import com.njuzr.eaibackend.utils.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/20 - 00:02
 * @Package: EAI-Backend
 */

@Service
public class MyUserDetailService implements UserDetailsService {

    private final UserMapper userMapper;

    @Autowired
    public MyUserDetailService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String officialNumber) throws UsernameNotFoundException {
        User user = userMapper.selectByOfficialNumber(officialNumber);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with officialNumber: " + officialNumber);
        }

        return ModelMapperUtil.map(user, MyUserDetails.class);
    }
}
