package com.multi.laptellect.common.service;


import com.multi.laptellect.common.model.dto.EmailDTO;
import com.multi.laptellect.config.EmailConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender javaMailSender;
    private final EmailConfig emailConfig;

    @Override
    public void sendEmail(EmailDTO emailDTO) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDTO.getReceiveAddress());
        message.setSubject(emailDTO.getMailTitle());
        message.setText(emailDTO.getMailContent());
        message.setFrom(emailConfig.getUsername());
        message.setReplyTo(emailConfig.getUsername());

        javaMailSender.send(message);
    }
}
