package com.qt.VideoPlatformAPI.File.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("storage")
@Getter
@Setter
public class StorageProperties {
    /**
     * Folder location for storing files
     */
    private String location = "videos-dir";
}
