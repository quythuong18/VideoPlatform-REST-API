package com.qt.VideoPlatformAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VideoPlatformApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoPlatformApiApplication.class, args);
	}

}
