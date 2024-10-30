package com.qt.VideoPlatformAPI.User;

import jakarta.persistence.*;

import java.util.List;


@Entity
public class UserFollowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne()
    @JoinColumn(name = "fk_user_profile_id")
    private UserProfile userProfile;
}
