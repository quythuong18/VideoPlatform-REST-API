package com.qt.VideoPlatformAPI.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotiMetadata {
    private String videoId;
    private String videoTitle;
    private String thumbnailUrl;

    private String commentId;
    private String comment;

    public NotiMetadata(String videoId, String videoTitle, String thumbnailUrl) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.thumbnailUrl = thumbnailUrl;
    }
}
