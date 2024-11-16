package com.qt.VideoPlatformAPI.File;

import com.qt.VideoPlatformAPI.Video.Video;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VideoFileProcessingService {
    FFmpegService fFmpegService;
    public void processVideo(Video video) {

    }
}
