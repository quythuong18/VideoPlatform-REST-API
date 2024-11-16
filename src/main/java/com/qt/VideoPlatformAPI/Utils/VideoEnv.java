package com.qt.VideoPlatformAPI.Utils;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Configuration
public class VideoEnv {
    public static final List<String> VIDEO_MIME_TYPES = Arrays.asList(
            "video/mp4",
            "video/mkv",
            "video/avi",
            "video/mpeg",
            "video/webm"
    );
    public static final Path ROOT_LOCATION = Paths.get("videos-dir");
    public static final List<Integer> VIDEO_QUALITY = Arrays.asList(144, 360, 480, 720, 1080);
    public static final int AUDIO_BITRATE = 256000;

    @Bean
    public FFmpeg fFmpeg() throws IOException {
        return new FFmpeg("/sbin/ffmpeg");
    }
    @Bean
    public FFprobe fFprobe() throws IOException {
        return new FFprobe("/sbin/ffprobe");
    }
}
