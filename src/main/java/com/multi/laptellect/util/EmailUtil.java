package com.multi.laptellect.util;

import com.multi.laptellect.common.model.Email;
import com.multi.laptellect.config.EmailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailUtil {
    private final JavaMailSender javaMailSender;
    private final EmailConfig emailConfig;

    public void sendEmail(Email email) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getReceiveAddress());
        message.setSubject(email.getMailTitle());
        message.setText(email.getMailContent());
        message.setFrom(emailConfig.getUsername());
        message.setReplyTo(emailConfig.getUsername());

        javaMailSender.send(message);
    }
}
