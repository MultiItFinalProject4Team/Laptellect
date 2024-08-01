package com.multi.laptellect.member.model.dto;

import lombok.Data;

/**
 * 회원 배송지 정보 DTO 객체
 *
 * @author : 이강석
 * @fileName : AddressDTO
 * @since : 2024-08-01
 */
@Data
public class AddressDTO {
    private int rowNum;
    private int addressId;
    private int memberNo;
    private String addressName;
    private String recipientName;
    private String postalCode;
    private String address;
    private String detailAddress;
    private String tel;
    private String request;
}
