package com.njuzr.eaibackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.dto.user.AdminRegisterDTO;
import com.njuzr.eaibackend.dto.user.UserDTO;
import com.njuzr.eaibackend.dto.user.UserRegisterDTO;
import com.njuzr.eaibackend.dto.user.UserUpdateDTO;
import com.njuzr.eaibackend.enums.Role;
import com.njuzr.eaibackend.po.User;
import com.njuzr.eaibackend.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author: Leonezhurui
 * @Date: 2024/2/15 - 09:00
 * @Package: EAI-Backend
 */

public interface UserService {

    /**
     * 创建用户
     * @param userDTO
     * @return UserVO
     */
    UserVO createUser(UserRegisterDTO userDTO);

    void sendVerifyCode(String officialEmail);

    void adminCreateUser(AdminRegisterDTO adminRegisterDTO);

    /**
     * 通过用户Id找到用户
     * @param id
     * @return UserVO
     */
    IPage<UserVO> searchUsers(Page<User> page, Long id, String name, String officialNumber, Role role);

    /**
     * 用户更新用户信息
     * @param id
     * @param userDTO
     * @return UserVO
     */
    UserVO updateUser(Long id, UserUpdateDTO userDTO);

    /**
     * 管理员更新用户信息
     * @param id
     * @param userDTO
     * @return
     */
    UserVO updateUser(Long id, UserDTO userDTO);

    /**
     * 用户更新密码
     * @param id
     * @param newPassword
     * @param oldPassword
     */
    void updatePassword(Long id, String newPassword, String oldPassword);

    /**
     * 管理员重置密码
     * @param id
     */
    void resetPassword(Long id);


    void deleteById(Long id);


    /**
     * 从Excel文件中读取数据，并批量创建用户
     * @param file
     * @throws Exception
     */
    void batchCreateUsersFromExcel(MultipartFile file) throws Exception;

    /**
     * 从Csv文件中读取数据，并批量创建用户
     * @param file
     * @throws Exception
     */
    void batchCreateUsersFromCsv(MultipartFile file) throws Exception;

}
