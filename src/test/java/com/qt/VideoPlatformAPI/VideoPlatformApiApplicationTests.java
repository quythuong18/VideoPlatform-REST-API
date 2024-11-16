package com.qt.VideoPlatformAPI;

import com.qt.VideoPlatformAPI.File.FFmpegService;
import com.qt.VideoPlatformAPI.User.UserService;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class VideoPlatformApiApplicationTests {

	private final FFmpegService fFmpegService = new FFmpegService(new FFmpeg("/sbin/ffmpeg"),
			new FFprobe("/sbin/ffprobe"));

	@Autowired
	private UserService userService;

    VideoPlatformApiApplicationTests(UserService userService) throws IOException {
        this.userService = userService;
    }

    @Test
	void test_ffprobe() throws IOException {
		fFmpegService.transcodeVideo(fFmpegService.getVideoFileMetadata("673060913c41642cc6987e7d"));
	}

	@Test
	void test_load_current_user() {
		userService.getCurrentUser();
	}

}
