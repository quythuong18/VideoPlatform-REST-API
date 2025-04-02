package com.qt.VideoPlatformAPI.Event;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class NotiMetadata {
    private String videoId;
    private String videoTitle;
    private String commentId;
    private String comment;

    public NotiMetadata(String videoId, String videoTitle) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
    }
}
