package com.example.piuda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PiudaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiudaApplication.class, args);
	}

}
