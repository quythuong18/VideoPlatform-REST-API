package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VideoService {
    private final IVideosRepository iVideoRepository;
    private final UserService userService;

    public Video addVideo(Video video) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfile user = userService.loadUserByUsername(username);

        // set more information for the
        video.setUserId(user.getId());
        video.setLikesCount(0L);
        video.setViewsCount(0L);

        return iVideoRepository.save(video);
    }
}
