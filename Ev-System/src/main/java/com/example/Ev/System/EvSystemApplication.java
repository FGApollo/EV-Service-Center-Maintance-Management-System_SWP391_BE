package com.example.Ev.System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EvSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvSystemApplication.class, args);
	}

}
