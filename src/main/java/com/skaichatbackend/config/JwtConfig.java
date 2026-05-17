package com.skaichatbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data

public class JwtConfig {
    // reads jwt.secret from application.properties
    private String secret;

    // reads jwt.expiration from application.properties (7 days in milliseconds)
    private long expiration;
}
