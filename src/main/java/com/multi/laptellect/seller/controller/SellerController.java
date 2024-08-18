package com.multi.laptellect.seller.controller;

import com.multi.laptellect.seller.service.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : SellerController
 * @since : 2024-08-18
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {
    private final SellerService sellerService;
}
