package com.qt.VideoPlatformAPI.Video.Like;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("video_likes")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VideoLike {
    @Id
    private String id;
    private ObjectId videoId;
    private Long userId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public void setVideoId(String videoId) {
        if(ObjectId.isValid(videoId)) {
            this.videoId = new ObjectId(videoId);
        } else throw new IllegalArgumentException("Invalid video id format");
    }
    public String getVideoId() {
        return this.videoId != null ? videoId.toHexString() : null;
    }

}
