package com.qt.VideoPlatformAPI.Event;

import java.util.List;
import com.qt.VideoPlatformAPI.Utils.NotificationTypes;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationEvent {
    private String fromUsername;
    private String fromUserProfilePic;
    private String fromUser;
    private List<String> toUsernames;
    private NotificationTypes type;
    private NotiMetadata notiMetadata;
}

