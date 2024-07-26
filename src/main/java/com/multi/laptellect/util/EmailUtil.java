package com.multi.laptellect.util;

import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.config.api.EmailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Email Util 클래스
 *
 * @author : 이강석
 * @fileName : EmailUtil.java
 * @since : 2024-07-26
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class EmailUtil {
    private final JavaMailSender javaMailSender;
    private final EmailConfig emailConfig;


    /**
     * 이메일 보내기 메서드
     *
     * @param email the email
     * @throws Exception the exception
     */
    public void sendEmail(Email email) throws Exception { // 이메일 전송 클래스
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getReceiveAddress());
        message.setSubject(email.getMailTitle());
        message.setText(email.getMailContent());
        message.setFrom(emailConfig.getUsername());
        message.setReplyTo(emailConfig.getUsername());

        javaMailSender.send(message);
    }
}
