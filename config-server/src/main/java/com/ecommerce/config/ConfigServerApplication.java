package com.ecommerce.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Bootstraps the centralized Config Server.
 *
 * Serves configuration to every other service from the {@code config-repo}
 * folder (native profile — plain files, no separate git repo to manage before
 * the deadline). Each downstream service asks for its config by its
 * {@code spring.application.name}, e.g. GET /order-service/default.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}