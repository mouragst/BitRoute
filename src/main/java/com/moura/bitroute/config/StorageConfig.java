package com.moura.bitroute.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "paste.storage")
@Data
public class StorageConfig {
    
    private String path = "./paste-storage";
}
