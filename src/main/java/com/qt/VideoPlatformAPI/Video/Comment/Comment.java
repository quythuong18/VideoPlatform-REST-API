package com.qt.VideoPlatformAPI.Video.Comment;

import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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
    private String videoId;
    private Long userId;
    @NotBlank(message = "Content is required")
    private String content;
    private String replyTo;
    private Long likeCount;
    private Long replyCount;
    private Boolean isEdited;
}
