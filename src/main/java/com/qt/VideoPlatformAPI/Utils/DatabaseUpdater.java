package com.qt.VideoPlatformAPI.Utils;

import com.qt.VideoPlatformAPI.User.IUserRepository;
import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Video.Comment.Comment;
import com.qt.VideoPlatformAPI.Video.Comment.ICommentRepository;
import com.qt.VideoPlatformAPI.Video.IVideosRepository;
import com.qt.VideoPlatformAPI.Video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseUpdater implements CommandLineRunner {
    private final IVideosRepository iVideosRepository;
    private final ICommentRepository iCommentRepository;
    private final IUserRepository iUserRepository;

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        update3NewfieldsInCommentModel();
        update3NewfieldsInVideoModel();
    }

    public void update3NewfieldsInVideoModel() {
        List<Video> allVideos = iVideosRepository.findAll();
        for(Video v : allVideos) {
            if(iUserRepository.existsById(v.getUserId())) {
                UserProfile user = userService.getUserByUserId(v.getUserId());
                v.setUsername(user.getUsername());
                v.setUserProfilePic(user.getProfilePic());
                v.setUserFullname(user.getFullName());
            }
        }
        iVideosRepository.saveAll(allVideos);
    }

    public void update3NewfieldsInCommentModel() {
        List<Comment> allComments = iCommentRepository.findAll();
        for(Comment c : allComments) {
            if(iUserRepository.existsById(c.getUserId())) {
                UserProfile user = userService.getUserByUserId(c.getUserId());
                c.setUsername(user.getUsername());
                c.setUserProfilePic(user.getProfilePic());
                c.setUserFullname(user.getFullName());
            }
        }
        iCommentRepository.saveAll(allComments);
    }
}
