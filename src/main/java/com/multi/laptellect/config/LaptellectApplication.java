package com.multi.laptellect.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching // 캐싱 설정
@EnableScheduling // 스케쥴러 설정
@SpringBootApplication
public class LaptellectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaptellectApplication.class, args);

	}

}
