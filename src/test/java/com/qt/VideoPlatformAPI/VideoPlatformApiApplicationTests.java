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

    VideoPlatformApiApplicationTests() throws IOException {
    }

    @Test
	void test_ffmpeg() throws IOException {
		fFmpegService.transcodeVideo(fFmpegService.getVideoFileMetadata("673060913c41642cc6987e7d"));
	}

	@Test
	void test_ffmpeg_manifestfile() throws IOException {
		fFmpegService.createManifestFile(fFmpegService.transcodeVideo(fFmpegService.getVideoFileMetadata("6744b91da5560704186ae0bb")));
	}

}
