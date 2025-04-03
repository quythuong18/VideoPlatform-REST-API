package com.qt.VideoPlatformAPI.File;

import com.qt.VideoPlatformAPI.Event.NotiMetadata;
import com.qt.VideoPlatformAPI.Event.NotificationEvent;
import com.qt.VideoPlatformAPI.Event.NotificationProducer;
import com.qt.VideoPlatformAPI.User.*;
import com.qt.VideoPlatformAPI.Utils.NotificationTypes;
import com.qt.VideoPlatformAPI.Utils.VideoConstants;
import com.qt.VideoPlatformAPI.Video.Video;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class VideoFileProcessingService {
    private final FFmpegService fFmpegService;
    private final IUserConnectionRepository iUserConnectionRepository;
    @Lazy private final VideoService videoService;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final NotificationProducer notificationProducer;

    @Async
    public void processVideoAsync(String videoId) {
        Video v = videoService.getVideoById(videoId);
        // process video: transcoding and creating manifest file
        try {
            VideoFileMetadata videoFileMetadata =
                    fFmpegService.createManifestFile(fFmpegService.transcodeVideo(fFmpegService.getVideoFileMetadata(videoId)));
            fFmpegService.createVideoThumbnailFrame(videoId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // create and update thumbnail
        videoService.updateVideoProcessedStatus(videoId);
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

        // sending notification event to rabbitmq
        sendingCompletionToRabbitMQ(v);
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

    public void sendingCompletionToRabbitMQ(Video v) {
        String ownerUsername = userService.getUserByUserId(v.getUserId()).getUsername();
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationTypes.NEW_VIDEO)
                .fromUsername(ownerUsername)
                .notiMetadata(new NotiMetadata(v.getId(), v.getTitle()))
                .build();
        Integer page = 0;
        Set<String> usernameList = new HashSet<>();
        do {
            usernameList = userService.getAllFollowersByUsername(ownerUsername, page, 10);
            notificationEvent.setToUsernames(usernameList.stream().toList());
            notificationProducer.sendMsg(notificationEvent);
            page++;
        } while(usernameList.size() >= 10);
    }
}
