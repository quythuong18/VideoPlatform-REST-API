package com.qt.VideoPlatformAPI.Video;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import jakarta.persistence.Id;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document("videos")
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Video extends TimeAudit {
    @Id
    private String id;

    private Long userId;
    private String username;
    private String userFullname;
    private String userProfilePic;

    private ObjectId playlistId; // deprecated
    private Set<ObjectId> playlistIds = new HashSet<>();
    private String title;
    private String description;
    private List<String> tags = new ArrayList<>();
    private String thumbnailUrl;
    private Long duration;
    private Long viewsCount;
    private Long likesCount;
    private Long CommentsCount;
    private Boolean isPrivate;
    private Boolean isCommentOff;
    private Boolean isUploaded;
    private Boolean isProcessed;

    public void setPlaylistIds(Set<String> playlistIds) {
        for(String id : playlistIds) {
            if(ObjectId.isValid(id)) {
                this.playlistIds.add(new ObjectId(id));
            } else throw new IllegalArgumentException("Invalid playlist id format");
        }
    }
    public Set<String> getPlaylistIds() {
        Set<String> playlistStringIds = new HashSet<>();

        for(ObjectId id : this.playlistIds) {
            if(id != null) playlistStringIds.add(id.toHexString());
            else playlistStringIds.add(null);
        }
        return playlistStringIds;
    }
}
