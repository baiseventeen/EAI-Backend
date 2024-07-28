package com.njuzr.eaibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njuzr.eaibackend.dto.user.AdminRegisterDTO;
import com.njuzr.eaibackend.dto.user.UserDTO;
import com.njuzr.eaibackend.dto.user.UserRegisterDTO;
import com.njuzr.eaibackend.dto.user.UserUpdateDTO;
import com.njuzr.eaibackend.enums.Role;
import com.njuzr.eaibackend.exception.MyException;
import com.njuzr.eaibackend.mapper.UserMapper;
import com.njuzr.eaibackend.po.User;
import com.njuzr.eaibackend.service.EmailService;
import com.njuzr.eaibackend.service.UserService;
import com.njuzr.eaibackend.utils.ModelMapperUtil;
import com.njuzr.eaibackend.utils.PageMapperUtil;
import com.njuzr.eaibackend.vo.UserVO;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/15 - 09:00
 * @Package: EAI-Backend
 */

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final EmailService emailService;

    private static final long SEND_INTERVAL = 60; // 允许再次发送的时间间隔，单位秒

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserServiceImpl(UserMapper userMapper, RedisTemplate<String, Object> redisTemplate, EmailService emailService) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }


    @Override
    public IPage<UserVO> searchUsers(Page<User> page, Long id, String name, String officialNumber, Role role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(); // 设置查询条件

        if(role == Role.ADMIN) // 禁止访问查询ADMIN用户
            throw new MyException(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase()+"："+"被拒绝");

        queryWrapper
                .eq(Objects.nonNull(id), "id", id)
                .like(StringUtils.isNotBlank(name), "name", name)
                .eq(StringUtils.isNotBlank(officialNumber), "official_number", officialNumber)
                .eq(Objects.nonNull(role), "role", role);

        IPage<User> targets = userMapper.selectPage(page,queryWrapper); // selectPage是内置的方法

        // targets.total如果为0，则说明没有符合条件的用户，但是不报错，由前端自行处理异常。

        return PageMapperUtil.convert(targets, record -> ModelMapperUtil.map(record, UserVO.class));
    }

    @Override
    public UserVO createUser(UserRegisterDTO userDTO) throws MyException{
        Role role = userDTO.getRole();
        if (role != Role.STUDENT) { // 只允许注册学生账号
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"权限不够");
        }

        // 从Redis中获取验证码
        final String key = "verifyCode:" + userDTO.getOfficialEmail();
        String storedCode = (String) redisTemplate.opsForValue().get(key);

        if (storedCode == null)
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"验证码已过期");

        // 验证码校验
        if (storedCode.equals(userDTO.getVerifyCode())) {
            User targetUser = ModelMapperUtil.map(userDTO, User.class);

            targetUser.setPassword(new BCryptPasswordEncoder().encode(targetUser.getPassword())); // 密码加密存储
            targetUser.setCreateTime(new Date()); // 设置创建时间

            if (isUserNotExists(targetUser.getOfficialNumber())) {
                int status = userMapper.insert(targetUser);
                if (status == 0) {
                    log.error("createUser -- 数据库插入错误：");
                    throw new MyException(501, "数据库插入错误");
                }
            }

            log.info("User创建完毕，User对象如下："+ targetUser);

            redisTemplate.delete(key); //注册成功后删除验证码

            return ModelMapperUtil.map(targetUser, UserVO.class);

        } else {
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"验证码错误");
        }
    }

    @Override
    public void sendVerifyCode(String officialEmail) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String intervalKey = "requestInterval:" + officialEmail;

        // 检查是否已经发送过验证码并且时间间隔未过
        if (ops.get(intervalKey) != null) {
            throw new MyException(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase()+"："+"需等待"+SEND_INTERVAL+"秒才能再次发送验证码");
        }

        // 生成验证码
        String code = UUID.randomUUID().toString().substring(0, 6);
        // 存储验证码到Redis，设置5分钟过期
        int timeout = 5;
        final String key = "verifyCode:" + officialEmail;
        // 存储验证码到Redis并设置过期时间
        redisTemplate.opsForValue().set(key, code, timeout, TimeUnit.MINUTES);
        // 设置请求间隔标记，防止频繁请求
        redisTemplate.opsForValue().set(intervalKey, "requested", SEND_INTERVAL, TimeUnit.SECONDS);

        emailService.sendVerifyCodeEmail(officialEmail, code, timeout);
    }

    /**
     * 管理员创建用户，给出name、officialEmail、officialNumber、role，随机生成密码，将密码通过邮件服务发送到用户邮箱
     * @param adminRegisterDTO
     * @return 1表示成功，0表示失败
     */
    @Override
    public void adminCreateUser(AdminRegisterDTO adminRegisterDTO) {
        Role role = adminRegisterDTO.getRole();
        if (role != Role.STUDENT && role != Role.TEACHER) { // 只能注册学生或老师账号
            throw new MyException(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase()+"："+"被拒绝");
        }

        User targetUser = ModelMapperUtil.map(adminRegisterDTO, User.class);

        // 随机生成密码
        String randomPassword = UUID.randomUUID().toString();
        targetUser.setPassword(passwordEncoder.encode(randomPassword));

        targetUser.setCreateTime(new Date());

        // 检查用户是否存在
        if (isUserNotExists(targetUser.getOfficialNumber())) {
            int code =  userMapper.insert(targetUser);
            if (code == 0) {
                log.error("createUser -- 数据库插入错误：");
                throw new MyException(501, "数据库插入错误");
            }
            log.info("User创建完毕，User对象如下："+ targetUser);
        }

        // 发送初始密码
        emailService.sendInitialPasswordEmail(targetUser.getOfficialEmail(), targetUser.getName(), randomPassword);

    }

    /**
     *
     * @param userDTO
     * @return
     */
    @Override
    public UserVO updateUser(Long id, UserUpdateDTO userDTO) {
        UserDTO opeUser = ModelMapperUtil.map(userDTO, UserDTO.class);
        return updateUser(id, opeUser);
    }

    @Override
    public UserVO updateUser(Long id, UserDTO userDTO) {
        User opeUser = ModelMapperUtil.map(userDTO, User.class);
        opeUser.setId(id);
        try {
            int status = userMapper.updateUser(opeUser);
            if (status > 0) {
                return ModelMapperUtil.map(userMapper.selectById(opeUser.getId()), UserVO.class);
            }
        }catch (Exception e) {
            log.error("数据库更新错误，错误如下："+e.getMessage());
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"数据库更新失败");
        }
        return null;
    }

    @Override
    public void updatePassword(Long id, String newPassword, String oldPassword) throws MyException{
        User user = userMapper.selectById(id);
        if (user == null) {
            log.error("用户不存在");
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"找不到用户");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.error("用户就密码不正确");
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"旧密码不正确");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        int code = userMapper.updatePassword(id, encodedNewPassword);
        if (code == 1) {
            log.info("用户更新成功");
        }
    }

    /**
     * 管理员重置密码，并将重置密码发送到指定邮箱
     * @param id
     */
    @Override
    public void resetPassword(Long id) {
        User targetUser = userMapper.selectById(id);
        if(targetUser == null) throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"用户不存在");
        String targetName = targetUser.getName();
        String targetEmail = targetUser.getOfficialEmail();
        String randomPassword = UUID.randomUUID().toString();
        int code = userMapper.updatePassword(id, passwordEncoder.encode(randomPassword));
        if (code == 1) {
            log.info("用户更新成功");
        }
        emailService.sendResetPasswordEmail(targetEmail, targetName, randomPassword);
    }


    @Override
    public void deleteById(Long id) {
        if (isUserExists(id)) {
            int res = userMapper.deleteById(id);
            if (res == 0)
                throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"删除失败");
        }
    }

    /**
     * 解析文件，批量创建用户。其中，文件必须包含姓名、邮箱、学号。
     * @param file
     * @return
     */
    @Override
    public void batchCreateUsersFromExcel(MultipartFile file) throws Exception {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // 默认只允许一张表
        Row headerRow = sheet.getRow(0);
        int nameCol = findColumnIndex(headerRow, "姓名");
        int numberCol = findColumnIndex(headerRow, "学号");
        int emailCol = findColumnIndex(headerRow, "邮箱");

        if(nameCol < 0 || numberCol < 0 || emailCol < 0)
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"缺失必要字段信息");

        List<User> users = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // 跳过表头

            User user = new User();
            user.setName(stringifyCellValue(row.getCell(nameCol)));
            user.setOfficialNumber(stringifyCellValue(row.getCell(numberCol)));
            user.setOfficialEmail(stringifyCellValue(row.getCell(emailCol)));
            user.setRole(Role.STUDENT); // 批量创建只创建学生账号
            user.setCreateTime(new Date());

            users.add(user);
        }

        processUsers(users);
    }

    @Override
    public void batchCreateUsersFromCsv(MultipartFile file) throws Exception {
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
        String[] header = csvReader.readNext();
        int nameCol = findColumnIndex(header, "姓名");
        int numberCol = findColumnIndex(header, "学号");
        int emailCol = findColumnIndex(header, "邮箱");

        if(nameCol < 0 || numberCol < 0 || emailCol < 0)
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"缺失必要字段信息");

        List<User> users = new ArrayList<>();
        String[] nextRecord;
        while ((nextRecord = csvReader.readNext()) != null) {
            String officialNumber = String.valueOf(nextRecord[numberCol]);
            if (isUserNotExists(officialNumber)) {
                User user = new User();
                user.setName(String.valueOf(nextRecord[nameCol]));
                user.setOfficialNumber(officialNumber);
                user.setOfficialEmail(String.valueOf(nextRecord[emailCol]));
                user.setRole(Role.STUDENT); // 批量创建只创建学生账号
                user.setCreateTime(new Date());

                users.add(user);
            }

        }

        // 统一生成随机密码，批量创建用户，创建成功后，将初始密码发送至邮箱
        processUsers(users);

    }


    /**
     * 查看用户是否不存在，如果用户不存在，则返回true
     * @param officialNumber
     * @return
     */
    private boolean isUserNotExists(String officialNumber) throws MyException{
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("official_number", officialNumber);
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            log.error("用户已存在，学号{}重复", officialNumber);
            throw new MyException(400, "用户已存在，请检查学号信息");
        }
        return true;
    }

    /**
     * 检查用户是否存在，如果存在则返回true；不存在则报错
     * @param id
     * @return
     * @throws MyException
     */
    private boolean isUserExists(Long id) throws MyException{
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser == null) {
            log.error("用户不存在，ID:{}", id);
            throw new MyException(400, "用户不存在，请检查ID信息");
        }
        return true;
    }


    /**
     * 在指定Excel Cell中找到具体列名的索引值
     * @param headerRow
     * @param columnName
     * @return
     */
    private int findColumnIndex(Row headerRow, String columnName) {
        for (Cell cell : headerRow) {
            if (cell.getStringCellValue().trim().equalsIgnoreCase(columnName)) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }

    private String stringifyCellValue(Cell cell) {
        String cellValue;
        if (cell.getCellType() == CellType.STRING) {
            cellValue = cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // 对于数值类型，可以将其转换为字符串
            // 注意：这里直接使用了Double.toString，你可能需要根据实际情况调整格式化方式
            cellValue = Double.toString(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            // 对于布尔类型，也可以转换为字符串
            cellValue = Boolean.toString(cell.getBooleanCellValue());
        } else {
            // 其他类型，根据需要处理或转换为字符串
            // 例如，对于公式类型，可以计算公式的值
            throw new MyException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"上传失败：请检查excel单元格的类型");
        }
        return cellValue;
    }

    /**
     * 在csv文件的header中找到具体列的索引值
     * @param header
     * @param columnName
     * @return
     */
    private int findColumnIndex(String[] header, String columnName) {
        for (int i = 0; i < header.length; i++) {
            if (header[i].trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 统一生成随机密码，批量创建用户，创建成功后，将初始密码发送至邮箱
     * @param users
     */
    private void processUsers(List<User> users) {
        List<String> passwordStage = new ArrayList<>();
        for(User user: users) {
            // 随机生成密码，并使用BCryptPasswordEncoder加密密码
            String randomPassword = UUID.randomUUID().toString();
            passwordStage.add(randomPassword);
            String encodedPassword = passwordEncoder.encode(randomPassword);
            user.setPassword(encodedPassword);
        }
        int res = userMapper.batchInsert(users);
        if (res == 0)
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(),HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"批量创建失败");

        for (int i = 0; i < users.size(); i++) {
            emailService.sendInitialPasswordEmail(users.get(i).getOfficialEmail(), users.get(i).getName(), passwordStage.get(i));
        }
    }

}
