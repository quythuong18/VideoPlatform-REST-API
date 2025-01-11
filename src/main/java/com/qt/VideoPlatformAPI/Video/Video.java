package com.qt.VideoPlatformAPI.Video;

import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import jakarta.persistence.Id;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document("video")
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Video extends TimeAudit {
    @Id
    private String id;
    private Long userId;
    private ObjectId playlistId;
    private String title;
    private String description;
    private List<String> tags;
    private String thumbnailUrl;
    private Long duration;
    private Long viewsCount;
    private Long likesCount;
    private Long CommentsCount;
    private Boolean isPrivate;
    private Boolean isCommentOff;
    private Boolean isUploaded;
    private Boolean isProcessed;

    public void setPlaylistId(String playlistId) {
        if(ObjectId.isValid(playlistId)) {
            this.playlistId = new ObjectId(playlistId);
        } else throw new IllegalArgumentException("Invalid playlist id format");
    }
    public String getPlaylistId() {
        return this.playlistId != null ? playlistId.toHexString() : null;
    }
}
