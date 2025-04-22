package com.qt.VideoPlatformAPI.Video.Like;

import com.qt.VideoPlatformAPI.Event.NotificationProducer;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.VideoService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class LikeService {
    private final ILikeRepository iLikeRepository;
    private final UserService userService;
    private final VideoService videoService;
    private final NotificationProducer notificationProducer;

    public void likeVideo(String videoId) {
        if(checkLikeVideo(videoId))
            throw new IllegalArgumentException("You've already liked this video");

        VideoLike videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userService.getCurrentUser().getId());

        iLikeRepository.save(videoLike);
        videoService.increaseLikeCount(videoId);
        notificationProducer.likeVideoEvent(userService.getCurrentUser().getUsername(),
            videoService.getUsernameByVideoId(videoId), videoId);
    }

    public void removeLikeVideo(String videoId) {
        if(!checkLikeVideo(videoId))
            throw new IllegalArgumentException("You have not liked this video before");
        Optional<VideoLike> videoIdOptional = iLikeRepository.findByVideoIdAndUserId(new ObjectId(videoId),
                userService.getCurrentUser().getId());

        videoIdOptional.ifPresent(iLikeRepository::delete);
        videoService.decreaseLikeCount(videoId);
    }

    public Boolean checkLikeVideo(String videoId) {
        Optional<VideoLike> videoIdOptional = iLikeRepository.findByVideoIdAndUserId(new ObjectId(videoId),
                userService.getCurrentUser().getId());
        return videoIdOptional.isPresent();
    }

    public List<String> getAllLikedVideoIds(Long userId, Integer page, Integer size) {
        List<String> videoIds = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<VideoLike> videoLikes = iLikeRepository.findByUserId(userId, pageable);
        for(VideoLike vl : videoLikes) {
            videoIds.add(vl.getVideoId());
        }
        return videoIds;
    }

    public void removeAllLikeOfAVideo(String videoId) {
        iLikeRepository.deleteAllByVideoId(videoId);
    }
}