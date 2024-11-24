package com.qt.VideoPlatformAPI.Video.Like;

import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class LikeService {
    private final ILikeRepository iLikeRepository;
    private final UserService userService;
    private final VideoService videoService;

    public void LikeVideo(String videoId) {
        if(checkLikeVideo(videoId))
            throw new IllegalArgumentException("You've already liked this video");

        VideoLike videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userService.getCurrentUser().getId());

        iLikeRepository.save(videoLike);
        videoService.increaseLikeCount(videoId);

    }

    public void removeLikeVideo(String videoId) {
        if(!checkLikeVideo(videoId))
            throw new IllegalArgumentException("You have not liked this video before");
        Optional<VideoLike> videoIdOptional = iLikeRepository.findByVideoIdAndUserId(videoId,
                userService.getCurrentUser().getId());

        videoIdOptional.ifPresent(iLikeRepository::delete);
        videoService.decreaseLikeCount(videoId);
    }

    public Boolean checkLikeVideo(String videoId) {
        Optional<VideoLike> videoIdOptional = iLikeRepository.findByVideoIdAndUserId(videoId,
                userService.getCurrentUser().getId());
        return videoIdOptional.isPresent();
    }
}
