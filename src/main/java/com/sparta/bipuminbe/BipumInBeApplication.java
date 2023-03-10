package com.sparta.bipuminbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BipumInBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BipumInBeApplication.class, args);
	}

}
