package com.quannv.carparkfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarParkFinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarParkFinderApplication.class, args);
	}

}
