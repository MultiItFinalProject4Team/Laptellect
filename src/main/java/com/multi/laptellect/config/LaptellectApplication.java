package com.multi.laptellect.config;

import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.service.CrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LaptellectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaptellectApplication.class, args);
main2(args);
	}
	@Autowired
	CrawlingService crawlingService;

	public static void main2(String[] args) {
		ProductDTO productDTO = new ProductDTO();

		CrawlingService.reviewCrawler(productDTO);

	}

}
