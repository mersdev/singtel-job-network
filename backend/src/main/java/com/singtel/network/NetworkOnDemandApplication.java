package com.singtel.network;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for Singtel Network On-Demand platform.
 * 
 * This application provides a self-service web portal for SMEs to manage
 * their network infrastructure with on-demand control and automation.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableTransactionManagement
public class NetworkOnDemandApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkOnDemandApplication.class, args);
    }
}
