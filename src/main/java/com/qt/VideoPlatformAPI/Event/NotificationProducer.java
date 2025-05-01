package com.qt.VideoPlatformAPI.Event;

import com.qt.VideoPlatformAPI.User.UserService;
import com.qt.VideoPlatformAPI.Utils.NotificationTypes;
import com.qt.VideoPlatformAPI.Video.Comment.Comment;
import com.qt.VideoPlatformAPI.Video.Video;
import com.qt.VideoPlatformAPI.Video.VideoService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class NotificationProducer {
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String notificationRoutingKey;

    private final RabbitTemplate rabbitTemplate;
    @Lazy
    @Autowired
    private UserService userService;
    @Lazy
    @Autowired
    private final VideoService videoService;

    public void sendMsg(NotificationEvent eventMessage) {
        rabbitTemplate.convertAndSend(exchangeName, notificationRoutingKey, eventMessage);
        System.out.println(eventMessage.toString());
    }

    @Async
    public void newVideoEvent(Video v) {
        String ownerUsername = userService.getUserByUserId(v.getUserId()).getUsername();
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationTypes.NEW_VIDEO)
                .fromUsername(ownerUsername)
                .notiMetadata(new NotiMetadata(v.getId(), v.getTitle()))
                .build();
        Integer page = 0;
        Set<String> usernameList;
        do {
            usernameList = userService.getAllFollowersByUsername(ownerUsername, page, 10);
            notificationEvent.setToUsernames(usernameList.stream().toList());
            sendMsg(notificationEvent);
            page++;
        } while(usernameList.size() >= 10);
    }

    @Async
    public void followingEvent(String followerUsername, String followingUsername) {
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationTypes.FOLLOW)
                .fromUsername(followerUsername)
                .toUsernames(List.of(followingUsername))
                .build();
        sendMsg(notificationEvent);
    }

    @Async
    public void likeVideoEvent(String fromUsername, String toOwner, String videoId) {
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationTypes.LIKE_VIDEO)
                .fromUsername(fromUsername)
                .toUsernames(List.of(toOwner))
                .notiMetadata(new NotiMetadata(videoId, videoService.getVideoById(videoId).getTitle()))
                .build();
        sendMsg(notificationEvent);
    }

    @Async
    public void commentEvent(Comment comment, Video video) {
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationTypes.COMMENT_ON_VIDEO)
                .fromUsername(comment.getUsername())
                .toUsernames(List.of(video.getUsername()))
                .notiMetadata(new NotiMetadata(video.getId(), video.getTitle()))
                .build();

        notificationEvent.getNotiMetadata().setCommentId(comment.getId());
        notificationEvent.getNotiMetadata().setComment(comment.getContent());
        sendMsg(notificationEvent);
    }

    @Async
    public void likeCommentEvent(String fromUsername, Comment comment, Video video) {
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationTypes.LIKE_COMMENT)
                .fromUsername(fromUsername)
                .toUsernames(List.of(comment.getUsername()))
                .notiMetadata(new NotiMetadata(video.getId(), video.getTitle(), comment.getId(), comment.getContent()))
                .build();
        sendMsg(notificationEvent);
    }
}
