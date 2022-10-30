package com.interview.accounts.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "spring.security.mcb")
public class SpringSecurityConfig {
    private String userName;
    private String password;
    private int tokenExpiry;
    private String secret;
}
