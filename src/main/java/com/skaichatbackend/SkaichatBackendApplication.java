package com.skaichatbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication

@EnableJpaRepositories(basePackages = {
        "com.skaichatbackend.user",
        "com.skaichatbackend.contact",
        "com.skaichatbackend.conversation",
        "com.skaichatbackend.settings"
})
public class SkaichatBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkaichatBackendApplication.class, args);
    }

}
