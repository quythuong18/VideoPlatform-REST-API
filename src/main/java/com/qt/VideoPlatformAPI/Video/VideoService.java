package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.File.CloudinaryService;
import com.qt.VideoPlatformAPI.File.VideoFileProcessingService;
import com.qt.VideoPlatformAPI.Playlist.Playlist;
import com.qt.VideoPlatformAPI.Playlist.PlaylistService;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.Comment.ICommentRepository;
import com.qt.VideoPlatformAPI.Video.Like.LikeService;
import com.qt.VideoPlatformAPI.Video.View.IViewHistoryRepository;
import com.qt.VideoPlatformAPI.Video.View.ViewHistory;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class VideoService {
    private final Logger logger;
    private final IVideosRepository iVideoRepository;
    private final UserService userService;
    private final ICommentRepository iCommentRepository;
    private final CloudinaryService cloudinaryService;
    private final CustomVideoRepository customVideoRepository;
    private final VideoFileProcessingService videoFileProcessingService;
    private final IViewHistoryRepository iViewHistoryRepository;
    @Lazy private final LikeService likeService;
    @Lazy private final PlaylistService playlistService;

    public Video addVideo(Video video) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfile user = userService.loadUserByUsername(username);

        // set more information for the video
        video.setUserId(user.getId());
        video.setUsername(user.getUsername());

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

    public String getUsernameByVideoId(String videoId) {
        return userService.getUserByUserId(getVideoById(videoId).getUserId()).getUsername();
    }

    public List<Video> getRandomVideos(Integer count) {
        return customVideoRepository.getRandomVideos(count);
    }

    public Boolean isVideoExistent(String videoId) {
        return iVideoRepository.existsById(videoId);
    }

    // authenticate video owner and guests
    public List<Video> getAllVideosByUserId(Long userId) {
        List<Video> videoList = iVideoRepository.findAllByUserId(userId);
        videoList.removeIf(v -> !v.getIsUploaded() && !v.getIsProcessed() && v.getIsPrivate());
        return videoList;
    }

    public List<Video> getAllLikedVideos(Integer page, Integer size) {
        UserProfile user = userService.getCurrentUser();
        List<String> videoIds = likeService.getAllLikedVideoIds(user.getId(), page, size);
        List<Video> videoList = new ArrayList<>();
        for(String id : videoIds) {
            videoList.add(getVideoById(id));
        }
        return videoList;
    }

    public void updatePlaylistId(String videoId, String playlistId) {
        Video video = getVideoById(videoId);
        video.setPlaylistId(playlistId);
        iVideoRepository.save(video);
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
            iCommentRepository.deleteAllByVideoId(videoId);
        }).thenAccept((res) -> {
            logger.info("Deleted all comments of video " + videoId);
        });

        // delete the video file
        CompletableFuture.runAsync(() -> {
            videoFileProcessingService.deleteVideoFiles(videoId);
        }).thenAccept((res) -> {
            logger.info("Deleted files of video " + videoId);
        });

        // delete the likes
        CompletableFuture.runAsync(() -> {
            likeService.removeAllLikeOfAVideo(videoId);
        }).thenAccept((res) -> {
            logger.info("Deleted all likes of video " + videoId);
        });

        //delete from the playlist
        CompletableFuture.runAsync(() -> {
            playlistService.deleteVideoFromPlaylist(video.getPlaylistId(), videoId);
        }).thenAccept((res) -> {
            logger.info("Deleted video from the playlist" + videoId);
        });

        //delete the video data
        iVideoRepository.deleteById(videoId);

    }

    public Long increaseView(String videoId) {
        Video video = getVideoById(videoId);
        video.setViewsCount(video.getViewsCount() + 1);
        iVideoRepository.save(video);
        return video.getViewsCount();
    }

    public ViewHistory addHistory(ViewHistory vh) {
        return iViewHistoryRepository.save(vh);
    }

}
