package com.lc.lc4jdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for LangChain4j demo application
 * Enables Spring Boot and scheduling capabilities
 * 
 * @author bing
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class Lc4jdemoApplication {

	/**
	 * Application entry point
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Lc4jdemoApplication.class, args);
	}

}
