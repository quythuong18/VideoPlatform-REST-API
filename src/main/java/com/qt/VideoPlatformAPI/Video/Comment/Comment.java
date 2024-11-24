package com.qt.VideoPlatformAPI.Video.Comment;

import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("comment")
public class Comment extends TimeAudit {
    @Id
    private String id;
    private String videoId;
    private Long userId;
    private Long replyTo;
    private List<CommentLike> likes;
    private List<Comment> replies;
    private Long replyCount;
    private Long likeCount;
}
