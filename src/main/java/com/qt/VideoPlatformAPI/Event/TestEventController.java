package com.qt.VideoPlatformAPI.Event;

import com.qt.VideoPlatformAPI.Utils.NotificationTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rabbitmq")
@RequiredArgsConstructor
public class TestEventController {
    private final NotificationProducer notificationProducer;

    @GetMapping("/sendMsg")
    public String sendMsg() {
        List<String> followers = new ArrayList<>();
        followers.add("user1");
        followers.add("user2");
        followers.add("user3");
        followers.add("user4");
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .type(NotificationTypes.NEW_VIDEO)
                .fromUsername("qthuong")
                .toUsernames(followers)
                .videoTitle("A video title")
                .build();
        notificationProducer.sendMsg(notificationEvent);
        return "OK";
    }
}
