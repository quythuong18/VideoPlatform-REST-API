package com.qt.VideoPlatformAPI.Video.View;

import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("views_history")
public class ViewHistory extends TimeAudit {
    String videoId;
    String videoTitle;
    String thumbnailUrl;
    Long ownerId;
    String ownerUsername;
    String ownerFullname;

    Long viewerId;
    String viewerUsername;
}
