package com.qt.VideoPlatformAPI.User;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class UserFollower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne()
    @JoinColumn(name = "fk_user_profile_id")
    private UserProfile user;

    @OneToMany()
    @JoinColumn(name = "fk_user_profile_id")
    private List<UserProfile> userFollowerList;
}
