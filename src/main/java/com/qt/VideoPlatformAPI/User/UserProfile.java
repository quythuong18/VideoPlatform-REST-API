package com.qt.VideoPlatformAPI.User;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends TimeAudit {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "profile_pic")
    private String profilePic;

    @Column(name = "bio")
    private String bio;

    @Column(name = "is_private")
    private Boolean isPrivate;

    @Column(name = "date_of_birth")
    private Instant dateOfBirth;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<UserVerification> userVerifications;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private UserFollower userFollower;

    @Column(name = "follower_count")
    private Long followerCount;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private UserFollowing userFollowing;

    @Column(name = "following_count")
    private Long followingCount;

}
