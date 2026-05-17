package com.skaichatbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.skaichatbackend.message")
public class MongoConfig{


    protected String getDatabaseName() {
        return "skaichat";
    }


    public boolean autoIndexCreation() {
        // auto create indexes we defined in Message.java
        return true;
    }
}
