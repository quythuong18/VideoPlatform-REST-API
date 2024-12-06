package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.File.CloudinaryService;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.VideoPlatformApiApplication;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class VideoService {
    private final IVideosRepository iVideoRepository;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    public Video addVideo(Video video) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfile user = userService.loadUserByUsername(username);

        // set more information for the video
        video.setUserId(user.getId());
        video.setLikesCount(0L);
        video.setViewsCount(0L);
        video.setCommentsCount(0L);
        video.setIsCommentOff(video.getIsCommentOff());

        video.setIsUploaded(false);
        video.setIsProcessed(false);

        return iVideoRepository.save(video);
    }

    public void updateVideoUploadedStatus(String videoId) {
        Video video = getVideoById(videoId);
        video.setIsUploaded(true);
        iVideoRepository.save(video);
    }

    public void updateVideoProcessedStatus(String videoId) {
        Video video = getVideoById(videoId);
        video.setIsProcessed(true);
        iVideoRepository.save(video);
    }

    public void increaseLikeCount(String videoId) {
        Video video = getVideoById(videoId);
        video.setLikesCount(video.getLikesCount() + 1);
        iVideoRepository.save(video);
    }

    public void decreaseLikeCount(String videoId) {
        Video video = getVideoById(videoId);
        video.setLikesCount(video.getLikesCount() - 1);
        iVideoRepository.save(video);
    }

    public void increaseCommentCount(String videoId) {
        Video video = getVideoById(videoId);
        video.setCommentsCount(video.getCommentsCount() + 1);
        iVideoRepository.save(video);
    }

    public void decreaseCommentCount(String videoId) {
        Video video = getVideoById(videoId);
        video.setCommentsCount(video.getCommentsCount() - 1);
        iVideoRepository.save(video);
    }

    public Video getVideoById(String videoId) {
        Optional<Video> video = iVideoRepository.findById(videoId);
        if(video.isEmpty())
            throw new IllegalArgumentException("Video data with id: " + videoId + " does not exist");

        return video.get();
    }

    public List<Video> getRandomVideos(Integer count) {
        return iVideoRepository.findRandomVideos(count);
    }

    public Boolean isVideoExistent(String videoId) {
        return iVideoRepository.existsById(videoId);
    }

    public List<Video> getAllVideosByUserId(Long userId) {
        List<Video> videoList = iVideoRepository.findAllByUserId(userId);
        videoList.removeIf(v -> !v.getIsUploaded() && !v.getIsProcessed() && v.getIsPrivate());
        return videoList;
    }

    public String updateThumbnailVideo(String videoId, MultipartFile file) throws IOException {
        Video video = getVideoById(videoId);
        String url;
        if(video.getThumbnailUrl() == null || video.getThumbnailUrl().isBlank())
            url = cloudinaryService.uploadPhoto(file, "thumbnail", videoId);
        else
            url = cloudinaryService.updatePhoto(file, "thumbnail", videoId);
        video.setThumbnailUrl(url);
        iVideoRepository.save(video);
        return url;
    }
}
