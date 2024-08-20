package com.multi.laptellect.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.multi.laptellect")
public class ContextConfiguration {
	

	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
		
	}
	
	@Bean
	public Gson gson() {
		
		return new Gson();
		
	}

	
	
}
