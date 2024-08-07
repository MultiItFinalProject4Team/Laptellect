package com.multi.laptellect.product.model.dto.laptop;

import lombok.Data;

/**
 * Please explain the class!!
 *
 * @author : 이우석
 * @fileName : AddOn
 * @since : 2024-07-30
 */
@Data
public class AddOn {
    private String os; // OS
    private String wirelessLan; // 무선랜
    private String usb; // USB 개수
    private String usbC; // USB C 개수
    private String usbA; // USB A 개수
    private String coolingfan; // 쿨링팬 유무
    private String bluetooth; // 블루투스
    private String thunderbolt; // 썬더볼트 단자
}
