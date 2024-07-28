package com.njuzr.eaibackend.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.po.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/15 - 08:59
 * @Package: EAI-Backend
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 通过学号（OfficialNumber）读取用户
     * @param officialNumber
     * @return User
     */
    User selectByOfficialNumber(String officialNumber);


    /**
     * 用户信息更新
     * @param user
     * @return 成功操作的数量，即1是成功，0是失败
     */
    int updateUser(User user);


    int updatePassword(Long id, String newPassword);

    /**
     * 批量导入用户
     * @param users 用户列表
     * @return 成功操作的数量
     */
    @Transactional
    int batchInsert(List<User> users);
}
