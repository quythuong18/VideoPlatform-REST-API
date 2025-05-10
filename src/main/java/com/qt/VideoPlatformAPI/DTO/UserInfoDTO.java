package com.qt.VideoPlatformAPI.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDTO {
    private Long Id;
    private String username;
    private String fullName;
    private String profilePic;
    private Boolean isFollowing;
}
