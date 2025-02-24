package com.qt.VideoPlatformAPI.Config;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FFmpegConfig {
    @Value("${spring.ffmpeg-path}")
    private String ffmpegPath;
    @Value("${spring.ffprobe-path}")
    private String ffprobePath;

    @Bean
    public FFmpeg fFmpeg() throws IOException {
        return new FFmpeg(ffmpegPath);
    }
    @Bean
    public FFprobe fFprobe() throws IOException {
        return new FFprobe(ffprobePath);
    }
}
