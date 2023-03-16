package com.sparta.bipuminbe;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(servers = {@Server(url = "https://bipum-in.shop", description = "Default Server URL") ,@Server(url = "http://localhost:8080", description = "Local Server URL")})
@EnableJpaAuditing
@SpringBootApplication
public class BipumInBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BipumInBeApplication.class, args);
	}

}
