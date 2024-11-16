package com.qt.VideoPlatformAPI;

import com.qt.VideoPlatformAPI.File.FFmpegService;
import lombok.AllArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class VideoPlatformApiApplicationTests {

	private final FFmpegService fFmpegService = new FFmpegService(new FFmpeg("/sbin/ffmpeg"),
			new FFprobe("/sbin/ffprobe"));

    VideoPlatformApiApplicationTests() throws IOException {
    }

    @Test
	void test_ffprobe() throws IOException {
		fFmpegService.transcodeVideo(fFmpegService.getVideoFileMetadata("673060913c41642cc6987e7d"));
	}

}
