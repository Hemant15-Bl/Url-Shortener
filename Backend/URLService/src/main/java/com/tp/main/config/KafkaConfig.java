package com.tp.main.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

	@Bean
    public NewTopic adviceTopic() {
        return TopicBuilder.name("url-clicks")
                .partitions(3) // High throughput
                .replicas(1)   // 1 for local, 3 for MAANG production
                .build();
    }
}
