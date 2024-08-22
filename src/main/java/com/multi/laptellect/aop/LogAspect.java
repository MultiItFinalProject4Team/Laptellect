package com.multi.laptellect.aop;

import com.multi.laptellect.common.service.LogService;
import com.multi.laptellect.member.model.dto.SocialDTO;
import com.multi.laptellect.member.model.mapper.MemberMapper;
import com.multi.laptellect.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * 로그 관련 AOP
 *
 * @author : 이강석
 * @fileName : LogAspect
 * @since : 2024-08-12
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    private final HttpServletRequest httpServletRequest;
    private final HttpSession httpSession;
    private final MemberMapper memberMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RedisUtil redisUtil;
    private final LogService logService;

    // 방문자 로그
    @Pointcut("within(com.multi.laptellect.main.controller.MainController) || " +
              "within(com.multi.laptellect.product.controller..*) || within(com.multi.laptellect.recommend.laptop.controller.RecommendationController)")
    public void visitControllers() {
    }

    // 상품 조회수
    @Pointcut("execution(* com.multi.laptellect.product.controller.ProductController.productLaptopDetails(..))")
    public void countProductMethod() {
    }

    @Pointcut("execution(* com.multi.laptellect.auth.service.OAuthServiceImpl.processKakaoUser(..)) || " +
              "execution(* com.multi.laptellect.auth.service.OAuthServiceImpl.processGoogleUser(..))")
    public void singIn() {}

    // 방문자 로그, 방문자 수 로그 AOP
    @After("visitControllers()")
    public void logVisit() {
        String sessionKey = "Visit:" + "count";
        // 방문한 적 없는 사용자 일 시 count를 올리고 세션에 true 값 할당
        if (httpSession.getAttribute(sessionKey) == null) {
            String countStr = redisUtil.getData(sessionKey);

            // Redis에 방문자 카운트 없을 시 0, 있을 시 count 수 반환
            int count = countStr != null ? Integer.parseInt(countStr) : 0;
            count++;

            redisUtil.setData(sessionKey, String.valueOf(count));

            // 세션에 조회 여부 기록
            httpSession.setAttribute(sessionKey, true);
            log.info("방문자 수 카운트 = {}", countStr);
        } else {
            log.info("세션 있음 방문자 수 카운트 = {}", redisUtil.getData(sessionKey));
        }

        // 접속 회원 로그
//        // IP 정보 기록
//        String ipAddress = httpServletRequest.getRemoteAddr();
//
//        // 접속 브라우저 기록
//        String userAgent = httpServletRequest.getHeader("User-Agent");
//
//        // 방문 시간 기록
//        String visitTime = LocalDateTime.now().format(formatter);
//
//        // 문자열로 통합
//        String visitLog = ipAddress + "|" + userAgent + "|" + visitTime;
//
//        // Redis 리스트 형태로 저장
//        redisUtil.setListData("VisitLog", visitLog);

//        log.info("방문 로그 기록 = {}" , redisUtil.getListData("VisitLog"));
    }

    // 상품 조회수 AOP
    @After("countProductMethod()")
    public void countProduct(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String productNo = String.valueOf((int) args[0]);

        String sessionKey = "Visit:" + "product" + productNo;
        String key = "Visit:product";

        // 방문한 적 없는 사용자 일 시 count를 올리고 세션에 true 값 할당
        if (httpSession.getAttribute(sessionKey) == null) { // 상품 조회 수

            String countStr = redisUtil.getHashData(key, productNo);

            int count = countStr != null ? Integer.parseInt(countStr) : 0;
            count++;

            redisUtil.setHashData(key, productNo, String.valueOf(count));

            // 세션에 조회 여부 기록
            httpSession.setAttribute(sessionKey, true);
            log.info("상품 조회수 카운트 = {}", countStr);
        } else {
            log.info("상품 조회수 카운트 세션 있음 = {}", redisUtil.getHashData(key, productNo));
        }
    }

    @After("singIn()") // 로그인 기록 AOP
    public void saveSignInLog(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        SocialDTO socialDTO = (SocialDTO) args[0];
        log.info("확인 = {}", socialDTO);

        try {
            String userName = memberMapper.findMemberBySocialEmail(socialDTO).getMemberName();
            logService.saveLoginLog(httpServletRequest, userName);
        } catch (Exception e) {
            log.info("로그인 로그 DB 저장 실패");
        }
    }
}
