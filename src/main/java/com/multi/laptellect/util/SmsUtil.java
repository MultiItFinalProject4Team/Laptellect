package com.multi.laptellect.util;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {
    @Value("${spring.coolsms.api.key}")
    private String apiKey;
    @Value("${spring.coolsms.api.secret}")
    private String apiSecretKey;

    @Value("${spring.coolsms.caller}")
    private String caller;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendOne(String phoneNum, String verifyCode) {
        Message message = new Message();
        message.setFrom(caller);
        message.setTo(phoneNum);
        message.setText("[Laptellect] 아래의 인증번호를 입력해주세요\n" + verifyCode);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }
}
