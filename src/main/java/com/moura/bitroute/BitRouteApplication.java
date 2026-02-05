package com.moura.bitroute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BitRouteApplication {

	public static void main(String[] args) {
		SpringApplication.run(BitRouteApplication.class, args);
	}

}
