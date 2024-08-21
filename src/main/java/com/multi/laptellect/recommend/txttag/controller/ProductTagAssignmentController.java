package com.multi.laptellect.recommend.txttag.controller;

import com.multi.laptellect.recommend.txttag.service.RecommenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductTagAssignmentController {
    private final RecommenService recommenService;

    /**
     * 노트북 태그화
     */
    @RequestMapping("/api/product-tags")
    public void onApplicationReady() {
        try {
            recommenService.assignTagsToProducts();
            log.info("노트북 태그화 성공");
        } catch (Exception e) {
            log.error("노트북 태그화 에러 = {}", e.getMessage());
        }
    }

    }
