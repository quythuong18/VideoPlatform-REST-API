package com.qt.VideoPlatformAPI.File;

import com.qt.VideoPlatformAPI.Config.VideoEnv;
import com.qt.VideoPlatformAPI.Video.Video;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class VideoFileProcessingService {
    FFmpegService fFmpegService;
    VideoService videoService;
    public void processVideoAsync(String videoId) {
        CompletableFuture.runAsync(() -> {
            try {
                VideoFileMetadata videoFileMetadata =
                fFmpegService.createManifestFile(fFmpegService.transcodeVideo(fFmpegService.getVideoFileMetadata(videoId)));
                fFmpegService.createVideoThumbnailFrame(videoId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).thenAccept((res) -> {
            videoService.updateVideoProcessedStatus(videoId);
            Video v = videoService.getVideoById(videoId);
            if(v.getThumbnailUrl() == null || v.getThumbnailUrl().isBlank()) {
                String filePath = VideoEnv.ROOT_LOCATION + "/" + videoId + "/thumbnail.jpg";
                File file = new File(filePath); // Locate the thumbnail file
                if (!file.exists()) {
                    throw new IllegalArgumentException("File not found at path: " + filePath);
                }
                try {
                    videoService.updateThumbnailVideo(videoId, (MultipartFile) file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
