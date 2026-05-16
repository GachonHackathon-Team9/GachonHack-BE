package com.example.GachonHack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GachonHackApplication {

	public static void main(String[] args) {
		SpringApplication.run(GachonHackApplication.class, args);
	}

}
