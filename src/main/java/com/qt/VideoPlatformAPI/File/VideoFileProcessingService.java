package com.qt.VideoPlatformAPI.File;

import com.qt.VideoPlatformAPI.Utils.VideoConstants;
import com.qt.VideoPlatformAPI.Video.Video;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class VideoFileProcessingService {
    private final FFmpegService fFmpegService;
    @Lazy private final VideoService videoService;
    private final CloudinaryService cloudinaryService;
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
                String filePath = VideoConstants.ROOT_LOCATION + "/" + videoId + "/thumbnail.jpg";
                File file = new File(filePath); // Locate the thumbnail file
                if (!file.exists()) {
                    throw new IllegalArgumentException("File not found at path: " + filePath);
                }

                try {
                    videoService.updateThumbnailUrl(videoId, cloudinaryService.uploadThumbnailFromVideo(file, videoId));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void deleteVideoFiles(String videoId) {
        deleteFile(new File(VideoConstants.ROOT_LOCATION.toString() + "/" + videoId));
    }
    public boolean deleteFile(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            if(files != null) {
                for(File f: files) {
                    deleteFile(f);
                }
            }
        }
        return file.delete();
    }
}
