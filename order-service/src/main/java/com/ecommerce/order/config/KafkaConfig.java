package com.ecommerce.order.config;

import com.ecommerce.contracts.OrderEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableScheduling
public class KafkaConfig {
    @Bean
    NewTopic ecommerceEventsTopic() {
        return TopicBuilder.name("ecommerce.events").partitions(6).replicas(1).build();
    }

}
