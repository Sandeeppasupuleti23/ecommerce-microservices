package com.ecommerce.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Bootstraps the Eureka Service Registry.
 *
 * Every other microservice in this platform (Config Server, API Gateway, Order,
 * Inventory, Payment, Notification) registers itself here on startup and discovers
 * peers by logical service name instead of hardcoded host:port values.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        System.out.println("hello");
    }
}