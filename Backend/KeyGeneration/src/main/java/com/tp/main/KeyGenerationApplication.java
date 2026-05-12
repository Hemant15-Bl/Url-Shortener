package com.tp.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.tp.main", "com.tp.main.security"})
public class KeyGenerationApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeyGenerationApplication.class, args);
	}

}
