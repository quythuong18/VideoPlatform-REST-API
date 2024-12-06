package com.qt.VideoPlatformAPI.DTO;


import lombok.Data;

@Data
public class UserPublicDTO {
    private Long Id;
    private String username;
    private String fullName;
    private String profilePic;
    private String dateOfBirth;
    private String followerCount;
    private String followingCount;
}