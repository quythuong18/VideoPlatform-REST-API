package com.qt.VideoPlatformAPI.Video.Comment;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("comment_likes")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CommentLike {
    @Id
    private String id;
    private ObjectId commentId;
    private Long userId;

    public void setCommentId(String commentId) {
        if(ObjectId.isValid(commentId)) {
            this.commentId = new ObjectId(commentId);
        } else throw new IllegalArgumentException("Invalid video id format");
    }
    public String getCommentId() {
        return this.commentId != null ? commentId.toHexString() : null;
    }
}
