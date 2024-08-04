package com.multi.laptellect.api.member;

import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.PointLogDTO;
import com.multi.laptellect.member.service.MemberService;
import com.multi.laptellect.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 회원 API 컨트롤러
 *
 * @author : 이강석
 * @fileName : MemberApiController
 * @since : 2024-08-01
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberService;

    /**
     * 회원 배송지 Insert
     *
     * @param addressDTO 배송지 정보가 담긴 DTO 객체
     * @return 반환 코드값
     */
    @ResponseBody
    @PostMapping("/insert-address")
    public int createMemberAddress(@RequestBody AddressDTO addressDTO){
        String addressName = addressDTO.getAddressName();
        try {
            // 배송지 명이 Null이면 Error 발생하므로 검증하기 위함
            if(addressName == null) {
                log.error("사용자 배송지 이름 미입력 = {}", addressName);
                return 0;
            }

            return memberService.createMemberAddress(addressDTO);
        } catch (Exception e) {
            log.error("배송지 Insert 실패 = ", e);
            return 500;
        }
    }

    /**
     * 배송지 리스트 반환
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/deliveryList")
    public String getAddressList(Model model){
        try {
            ArrayList<AddressDTO> userAddressList = memberService.findAllAddressByMemberNo();
            model.addAttribute("addressList", userAddressList);
        } catch (Exception e) {
            model.addAttribute("message", "배송지 리스트 조회 실패");
        }
        return "/member/delivery/address-list";
    }

    /**
     * 배송지 단일 조회
     *
     * @param addressId 배송지 PK키
     * @return addressDTO 객체 반환
     */
    @ResponseBody
    @GetMapping("/delivery")
    public AddressDTO getAddressById(@RequestParam("addressId") int addressId) {
        try {
            return memberService.findAddressByAddressId(addressId);
        } catch (Exception e) {
            log.error("배송지 조회 실패 = ", e);
            return null;
        }
    }

    /**
     * 회원 배송지 업데이트
     *
     * @param addressDTO 배송지 정보가 담긴 DTO
     * @return 숫자 코드 반환
     */
    @ResponseBody
    @PostMapping("/update-address")
    public int updateMemberAddress(@RequestBody AddressDTO addressDTO) {
        String addressName = addressDTO.getAddressName();
        log.info("업데이트 API 시작 = {}", addressDTO);

        try {
            // 배송지 명이 Null이면 업데이트 거부
            if(addressName == null) {
                log.error("사용자 배송지 이름 미입력 = {}", addressName);
                return 0;
            }

            return memberService.updateMemberAddress(addressDTO);
        } catch (Exception e) {
            log.error("배송지 UPDATE 실패 = ", e);
            return 500;
        }
    }

    /**
     * 회원 배송지 삭제
     *
     * @param addressId 배송지 PK키값
     * @return the boolean
     */
    @ResponseBody
    @PostMapping("/delete-address")
    public boolean deleteMemberAddress(@RequestParam("addressId") int addressId) {
        try {
            log.debug("배송지 DELETE 시작 = {}", addressId);
            if(memberService.deleteMemberAddress(addressId)) {
                log.info("배송지 DELETE 성공 = {}", addressId);
                return true;
            } else {
                log.error("배송지 DELETE 실패 = {}", addressId);
                return false;
            }

        } catch (Exception e) {
            log.error("배송지 DELETE 실패 = ", e);
            return false;
        }
    }

    @GetMapping("/all-point-list")
    public String getPointList(Model model, @PageableDefault(size = 10) Pageable pageable) {
        try {
            log.debug("포인트 내역 전체 조회 시작");
//            ArrayList<PointLogDTO> pointList = memberService.getAllPointList(pageable);
            Page<PointLogDTO> pointList = memberService.getAllPointList(pageable);
            log.info("포인트 내역 조회 성공 = {}", pointList.getContent());

            int page = pageable.getPageNumber();
            int size = pageable.getPageSize();

            int startPage = PaginationUtil.getStartPage(pointList);
            int endPage = PaginationUtil.getEndPage(pointList);

            model.addAttribute("pointList", pointList);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
        } catch (Exception e) {
            log.error("포인트 조회 실패 = ", e);
        }
        return "/member/point/all-point-tab";
    }



}
