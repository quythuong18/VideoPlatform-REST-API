package com.qt.VideoPlatformAPI.Video.Comment;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("comment_like")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CommentLike {
    @Id
    private String id;
    private String commentId;
    private Long userId;
}
