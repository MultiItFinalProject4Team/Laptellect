package com.multi.laptellect.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class LaptellectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaptellectApplication.class, args);

	}

}
