package com.spanishcoders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@EntityScan(basePackageClasses = { PelukappApplication.class, Jsr310JpaConverters.class })
@SpringBootApplication
public class PelukappApplication {

	public static void main(String[] args) {
		SpringApplication.run(PelukappApplication.class, args);
	}
}
