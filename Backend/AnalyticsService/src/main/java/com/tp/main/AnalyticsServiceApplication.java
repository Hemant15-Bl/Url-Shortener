package com.tp.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class AnalyticsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyticsServiceApplication.class, args);
	}

//	@Bean
//	public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
//		return new MongoTemplate(mongoDatabaseFactory);
//	}
}
