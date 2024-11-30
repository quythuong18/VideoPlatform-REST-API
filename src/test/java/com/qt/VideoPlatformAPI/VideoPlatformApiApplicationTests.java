package com.qt.VideoPlatformAPI;

import com.qt.VideoPlatformAPI.File.FFmpegService;
import com.qt.VideoPlatformAPI.File.VideoFileMetadata;
import com.qt.VideoPlatformAPI.User.UserService;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class VideoPlatformApiApplicationTests {
    @Autowired
    FFmpegService fFmpegService;

    @Test
    public void testThumbnail() throws IOException {
        fFmpegService.getVideoThumbnailFrame("6744950cf882237fb9bd833e");
    }
}
