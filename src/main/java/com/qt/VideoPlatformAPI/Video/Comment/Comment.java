package com.qt.VideoPlatformAPI.Video.Comment;

import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("comment")
@Getter
@Setter
@AllArgsConstructor
public class Comment extends TimeAudit {
    @Id
    private String id;
    @NotBlank(message = "Video id is required")
    private ObjectId videoId;
    private Long userId;
    @NotBlank(message = "Content is required")
    private String content;
    private ObjectId replyTo;
    private List<ObjectId> replies;
    private Long likeCount;
    private Long replyCount;
    private Boolean isEdited;

    public void setVideoId(String videoId) {
        if(ObjectId.isValid(videoId)) {
            this.videoId = new ObjectId(videoId);
        } else throw new IllegalArgumentException("Invalid video id format");
    }
    public String getVideoId() {
        return this.videoId != null ? videoId.toHexString() : null;
    }

    public void setReplyTo(String replyTo) {
        if(ObjectId.isValid(replyTo)) {
            this.replyTo = new ObjectId(replyTo);
        } else throw new IllegalArgumentException("Invalid comment(replyTo) id format");
    }
    public String getReplyTo() {
        return this.replyTo != null ? replyTo.toHexString() : null;
    }
}
