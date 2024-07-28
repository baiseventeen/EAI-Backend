package com.njuzr.eaibackend.controller;

import com.njuzr.eaibackend.dto.user.UserLoginDTO;
import com.njuzr.eaibackend.po.MyUserDetails;
import com.njuzr.eaibackend.service.UserService;
import com.njuzr.eaibackend.utils.JWTTokenUtil;
import com.njuzr.eaibackend.utils.ModelMapperUtil;
import com.njuzr.eaibackend.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/20 - 00:10
 * @Package: EAI-Backend
 */

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private final JWTTokenUtil jwtTokenUtil;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserService userService, JWTTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * 用户登陆学号(officialNumber)和密码(password)进行登陆
     * @param userLoginDTO officialNumber、password
     * @return 如果成功登陆，则返回登陆用户的身份信息和Token；如果失败，则返回具体原因。
     */
    @PostMapping("/login")
    public MyResponse login(@RequestBody UserLoginDTO userLoginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginDTO.getOfficialNumber(),
                            userLoginDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

            log.info("用户认证通过，用户认证信息如下：" + userDetails.toString());

            String jwt = jwtTokenUtil.generateToken(userDetails.getOfficialNumber());
            log.info("生成jwt成功，token信息：" + jwt);

            UserLoginVO userLoginVO = ModelMapperUtil.map(userDetails, UserLoginVO.class);
            userLoginVO.setToken(jwt);
            return MyResponse.success(userLoginVO);

        } catch (BadCredentialsException e) { // 密码出错抛出的异常
            log.error("登陆失败，密码错误");
            return MyResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"用户名或密码错误");
        } catch (UsernameNotFoundException e) {// 找不到用户抛出的异常
            log.error("登陆失败，找不到该用户");
            return MyResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()+"："+"用户名或密码错误");
        }
    }

}
