package com.multi.laptellect.util;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SMS Util 클래스
 *
 * @author : 이강석
 * @fileName : SmsUtil.java
 * @since : 2024-07-26
 */
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

    /**
     * SMS 인증 문자를 보내기 위한 메서드
     *
     * @param phoneNum   발신자 번호
     * @param verifyCode 보낼 코드
     * @return the single message sent response
     */
    public SingleMessageSentResponse sendOne(String phoneNum, String verifyCode) {
        Message message = new Message();
        message.setFrom(caller);
        message.setTo(phoneNum);
        message.setText("[Laptellect] 아래의 인증번호를 입력해주세요\n" + verifyCode);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }
}
