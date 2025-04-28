package com.qt.VideoPlatformAPI.File;

import com.qt.VideoPlatformAPI.Event.NotificationProducer;
import com.qt.VideoPlatformAPI.Utils.VideoConstants;
import com.qt.VideoPlatformAPI.Video.IVideosRepository;
import com.qt.VideoPlatformAPI.Video.Video;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class VideoFileProcessingService {
    private final FFmpegService fFmpegService;
    @Lazy private final VideoService videoService;
    private final IVideosRepository iVideosRepository;
    private final CloudinaryService cloudinaryService;
    private final NotificationProducer notificationProducer;

    @Async
    public void processVideoAsync(String videoId) throws IOException {
        Video video = videoService.getVideoById(videoId);
        // process video: transcoding and creating manifest file
        try {
            VideoFileMetadata videoFileMetadata =
                    fFmpegService.createManifestFile(fFmpegService.transcodeVideo(fFmpegService.getVideoFileMetadata(videoId)));
            fFmpegService.createVideoThumbnailFrame(videoId);
            video.setDuration(Math.round(videoFileMetadata.getDuration())); // set video duration in db
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // create and update thumbnail
        video.setIsProcessed(true);
        if(video.getThumbnailUrl() == null || video.getThumbnailUrl().isBlank()) {
            String filePath = VideoConstants.ROOT_LOCATION + "/" + videoId + "/thumbnail.jpg";
            File file = new File(filePath); // Locate the thumbnail file
            if (!file.exists()) {
                throw new IllegalArgumentException("File not found at path: " + filePath);
            }

            String thumbnailUrl = cloudinaryService.uploadThumbnailFromVideo(file, videoId);
            video.setThumbnailUrl(thumbnailUrl);

            iVideosRepository.save(video);
        }

        // sending notification event to rabbitmq
        notificationProducer.newVideoEvent(video);
    }

    public void deleteVideoFiles(String videoId) {
        deleteFile(new File(VideoConstants.ROOT_LOCATION.toString() + "/" + videoId));
    }

    public void deleteFile(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            if(files != null) {
                for(File f: files) {
                    deleteFile(f);
                }
            }
        }
        file.delete();
    }
}
