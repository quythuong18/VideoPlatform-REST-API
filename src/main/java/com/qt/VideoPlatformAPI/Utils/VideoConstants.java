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

public final class VideoConstants {
    public static final List<String> VIDEO_MIME_TYPES = Arrays.asList(
            "video/mp4",
            "video/mkv",
            "video/avi",
            "video/mpeg",
            "video/webm"
    );
    public static final List<String> IMAGE_MIME_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp"
    );
    public static final Path ROOT_LOCATION = Paths.get("videos-dir");
    public static final List<Integer> VIDEO_QUALITY = Arrays.asList(1080, 720, 480, 360, 144);
    public static final int AUDIO_BITRATE = 256000;
}
