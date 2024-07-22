package com.multi.laptellect.common.service;

import com.multi.laptellect.common.model.dto.EmailDTO;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmail(EmailDTO emailDTO) throws Exception;
}
