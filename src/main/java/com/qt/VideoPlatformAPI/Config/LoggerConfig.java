package com.qt.VideoPlatformAPI.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class LoggerConfig {
    @Bean
    public Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }
}
