package com.lc.lc4jdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Lc4jdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(Lc4jdemoApplication.class, args);
	}

}
