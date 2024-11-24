package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class VideoService {
    private final IVideosRepository iVideoRepository;
    private final UserService userService;

    public Video addVideo(Video video) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfile user = userService.loadUserByUsername(username);

        // set more information for the video
        video.setUserId(user.getId());
        video.setLikesCount(0L);
        video.setViewsCount(0L);

        video.setIsUploaded(false);
        video.setIsProcessed(false);

        return iVideoRepository.save(video);
    }

    public Video updateVideoUploadedStatus(Video video) {
        video.setIsUploaded(true);
        return iVideoRepository.save(video);
    }

    public Video updateVideoProcessedStatus(Video video) {
        video.setIsProcessed(true);
        return iVideoRepository.save(video);
    }

    public void increaseLikeCount(String id) {
        Video video = getVideoById(id);
        video.setLikesCount(video.getLikesCount() + 1);
        iVideoRepository.save(video);
    }

    public void decreaseLikeCount(String id) {
        Video video = getVideoById(id);
        video.setLikesCount(video.getLikesCount() - 1);
        iVideoRepository.save(video);
    }

    public Video getVideoById(String id) {
        Optional<Video> video = iVideoRepository.findById(id);
        if(video.isEmpty())
            throw new IllegalArgumentException("Video data with id = " + id + "does not exist");

        return video.get();
    }
}
