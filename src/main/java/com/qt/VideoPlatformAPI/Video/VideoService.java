package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.File.CloudinaryService;
import com.qt.VideoPlatformAPI.File.VideoFileProcessingService;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.Comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class VideoService {
    private static final Logger logger = Logger.getLogger(VideoService.class.getName());
    private final IVideosRepository iVideoRepository;
    private final UserService userService;
    @Lazy private final CommentService commentService;
    private final CloudinaryService cloudinaryService;
    private final CustomVideoRepository customVideoRepository;
    @Lazy private final VideoFileProcessingService videoFileProcessingService;

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

    public Video updateVideoInfo(Video newVideoInfo) {
        UserProfile currentUser = userService.getCurrentUser();
        Video updatedVideo = getVideoById(newVideoInfo.getId());

        if(!Objects.equals(currentUser.getId(), updatedVideo.getUserId()))
            throw new AccessDeniedException("You are not authorized to update this video");

        if(newVideoInfo.getTitle() != null)
            updatedVideo.setTitle(newVideoInfo.getTitle());
        if(newVideoInfo.getDescription() != null)
            updatedVideo.setDescription(newVideoInfo.getDescription());
        if(newVideoInfo.getIsPrivate() != null)
            updatedVideo.setIsPrivate(newVideoInfo.getIsPrivate());
        if(newVideoInfo.getIsCommentOff() != null)
            updatedVideo.setIsCommentOff(newVideoInfo.getIsCommentOff());
        if(newVideoInfo.getTags() != null)
            updatedVideo.setTags(newVideoInfo.getTags());

        return iVideoRepository.save(updatedVideo);
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
        return customVideoRepository.getRandomVideos(count);
    }

    public Boolean isVideoExistent(String videoId) {
        return iVideoRepository.existsById(videoId);
    }

    public List<Video> getAllVideosByUserId(Long userId) {
        List<Video> videoList = iVideoRepository.findAllByUserId(userId);
        videoList.removeIf(v -> !v.getIsUploaded() && !v.getIsProcessed() && v.getIsPrivate());
        return videoList;
    }

    public String uploadThumbnailVideo(String videoId, MultipartFile file) throws IOException {
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

    public void updateThumbnailUrl(String videoId, String url) {
        Video video = getVideoById(videoId);
        video.setThumbnailUrl(url);
        iVideoRepository.save(video);
    }

    public List<Video> searchByVideoTitle(String pattern, Integer count) {
        if(pattern == null || count == null || count <= 0)
            throw new IllegalArgumentException("Invalid input param");
        return customVideoRepository.searchByTitle(pattern, count);
    }

    public List<Video> searchByVideoTag(String tag, Integer count) {
        if(tag == null || count == null || count <= 0)
            throw new IllegalArgumentException("Invalid input param");
        return customVideoRepository.searchByTag(tag, count);
    }

    public void deleteVideo(String videoId) {
        Video video = getVideoById(videoId);
        if(!Objects.equals(userService.getCurrentUser().getId(), video.getUserId())) {
            throw new AccessDeniedException("You are not authorized to delete this video");
        }
        // delete the comments
        CompletableFuture.runAsync(() -> {
            commentService.deleteAllCommentsByVideoId(videoId);
        }).thenAccept((res) -> {
            logger.info("Deleted all comments of video " + videoId);
        });

        // delete the video file
        CompletableFuture.runAsync(() -> {
            videoFileProcessingService.deleteVideoFiles(videoId);
        }).thenAccept((res) -> {
            logger.info("Deleted files of video " + videoId);
        });

        //delete the video data
        iVideoRepository.deleteById(videoId);

    }
}
