package com.multi.laptellect.admin.model.dto;

import lombok.Data;

@Data
public class AdminQuestionDTO {
    private Long questionNo;
    private String questionType;
    private String title;
    private String memberName;
    private String categoryName;
    private String createdAt;
    private String answerStatus;
    private String productName;
    private String content;
}