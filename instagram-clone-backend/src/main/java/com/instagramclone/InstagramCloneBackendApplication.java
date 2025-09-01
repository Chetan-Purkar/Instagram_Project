package com.instagramclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.instagramclone.model")
@EnableJpaRepositories(basePackages = "com.instagramclone.repository")
public class InstagramCloneBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstagramCloneBackendApplication.class, args);
	}

}
