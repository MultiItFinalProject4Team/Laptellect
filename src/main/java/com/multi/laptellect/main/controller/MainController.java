package com.multi.laptellect.main.controller;

import com.multi.laptellect.recommend.txttag.config.CpuConfig;
import com.multi.laptellect.recommend.txttag.config.GpuConfig;
import com.multi.laptellect.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {
    private final RedisUtil redisUtil;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CpuConfig cpuConfig;
    private final GpuConfig gpuConfig;

    @GetMapping("/")
    public String main(HttpSession httpSession, HttpServletRequest httpServletRequest) {
//        Map<String, Integer> gpus = gpuConfig.getGpuMark();
//        Map<String, Integer> cpus = cpuConfig.getCpuMark();
//
//        Pattern pattern = Pattern.compile("[\\s()]+");
//        String Key = pattern.matcher("125U (4.3GHz)").replaceAll("");
//
////        Integer gpuValue = gpus.get(gpuKey);
//        Integer cpuValue = cpus.get(Key);
//        log.info("지퓨 확인 {}", cpuValue);
        return "common/main";
    }

    @GetMapping("/hello")
    public String hellopage(){
        return "hello";
    }



}