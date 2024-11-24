package com.qt.VideoPlatformAPI.Video.Like;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("videoLike")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VideoLike {
    @Id
    private String id;
    private String videoId;
    private Long userId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}
