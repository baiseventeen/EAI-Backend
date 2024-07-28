package com.njuzr.eaibackend.service;

import com.njuzr.eaibackend.exception.MyException;
import com.njuzr.eaibackend.utils.WebClientUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * @author: Leonezhurui
 * @Date: 2024/2/21 - 20:35
 * @Package: EAI-Backend
 */

@Slf4j
@Service
public class EmailService {
    private final WebClientUtil webClientUtil;

    @Autowired
    public EmailService(WebClientUtil webClientUtil) {
        this.webClientUtil = webClientUtil;
    }

    private void sendEmail(MyRequestObject requestObject) {
        MyResponseObject response = webClientUtil.post("https://message.seec.seecoder.cn/message/mail", requestObject, MyResponseObject.class);
        if (response.code == 1) {
            log.info("WebClient请求成功，邮件发送成功！");
        } else {
            log.error("WebClient请求失败，邮件发送失败~");
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()+"："+"邮件发送失败");
        }
    }

    /**
     * 创建账号后发送初始密码
     * @param to
     * @param username
     * @param password
     */
    public void sendInitialPasswordEmail(String to, String username, String password) {
        sendEmail(new MyRequestObject(
                new String[]{to},
                "Your New Account",
                "<h1>欢迎来到EAI天地！</h1>" +
                        "<p>亲爱的<b>" + username + "</b>同学,</p>" +
                        "<p>你的EAI账号已经创建，初始密码是<b>" + password + "</b></p>" +
                        "<p>请在登陆后尽快修改密码！</p>" +
                        "<p><a href='https://example.com/login'>登陆的虫洞～</a></p>" +
                        "<p>祝你的专业写作能力不断提升,<br>Seecoder EAI开发团队</p>"
        ));
    }

    /**
     * 创建账号后发送初始密码
     * @param to
     * @param username
     * @param password
     */
    public void sendResetPasswordEmail(String to, String username, String password) {
        sendEmail(new MyRequestObject(
                new String[]{to},
                "Your Reset Password",
                "<h1>欢迎来到EAI天地！</h1>" +
                        "<p>亲爱的<b>" + username + "</b>同学,</p>" +
                        "<p>你的密码已被重置，重置后的密码是<b>" + password + "</b></p>" +
                        "<p><a href='https://example.com/login'>登陆的虫洞～</a></p>" +
                        "<p>祝你的专业写作能力不断提升,<br>Seecoder EAI开发团队</p>"
        ));
    }


    public void sendVerifyCodeEmail(String to, String code, int timeout) {
        sendEmail(new MyRequestObject(
                new String[]{to},
                "Your Verify Code",
                "<h1>欢迎来到EAI天地！</h1>" +
                        "<p>亲爱的同学,</p>" +
                        "<p>你的邮箱验证是<b>" + code + "</b></p>" +
                        "<p>请尽快完成注册流程，邮箱验证码有效时间为<b>"+timeout+"分钟</b></p>" +
                        "<p>祝你的专业写作能力不断提升,<br>Seecoder EAI开发团队</p>"
        ));
    }


    @Data
    @AllArgsConstructor
    static class MyRequestObject {
        private String[] receivers;
        private String subject;
        private String text;
    }

    @Data
    static class MyResponseObject {
        private int code;
        private String msg;
        private Object data;
    }


//    private final JavaMailSender javaMailSender = new JavaMailSenderImpl();
//
//    @Value("${spring.mail.username}")
//    private String sourceEmail;

//    /**
//     * FIXME 邮箱服务暂时不可用，需要Seecoder的公邮和stmp秘钥
//     * @param to
//     * @param username
//     * @param password
//     * @return
//     * @throws MessagingException
//     */
//    public int sendPasswordEmail(String to, String username, String password) throws MessagingException {
//        try {
//            MimeMessage message = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setFrom(sourceEmail);
//            helper.setTo(to);
//            helper.setSubject("Your New Account");
//
//            String htmlContent = "<h1>欢迎来到EAI天地！</h1>" +
//                    "<p>亲爱的<b>" + username + "</b>同学,</p>" +
//                    "<p>你的EAI账号已经创建，初始密码是<b>" + password + "</b></p>" +
//                    "<p>请在登陆后尽快修改密码！</p>" +
//                    "<p><a href='https://example.com/login'>登陆的虫洞～</a></p>" +
//                    "<p>祝你的专业写作能力不断提升,<br>Seecoder EAI开发团队</p>";
//
//            helper.setText(htmlContent, true);
//
//            javaMailSender.send(message);
//            log.info("邮件发送成功！==> 收件人：{}", username);
//            return 1;
//        } catch (Exception e) {
//            log.error("邮件发送失败！==> {}",e.getMessage());
//            return 0;
//        }
//    }

}
