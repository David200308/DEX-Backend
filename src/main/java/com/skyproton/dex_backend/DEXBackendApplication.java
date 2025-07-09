package com.skyproton.dex_backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DEXBackendApplication {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(DEXBackendApplication.class);
		SpringApplication.run(DEXBackendApplication.class, args);
		logger.info("Starting the application");
	}

}
