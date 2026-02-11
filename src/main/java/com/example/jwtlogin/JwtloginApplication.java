package com.example.jwtlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JwtloginApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtloginApplication.class, args);
	}

}
