package com.ecommerce.payment.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {
    @Bean
    DefaultErrorHandler kafkaErrorHandler() {
        return new DefaultErrorHandler((ConsumerRecord<?, ?> record, Exception failure) -> {
        }, new FixedBackOff(1000L, FixedBackOff.UNLIMITED_ATTEMPTS));
    }
}