package com.qt.VideoPlatformAPI.User;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import com.qt.VideoPlatformAPI.Verification.UserVerification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends TimeAudit implements UserDetails {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "username is required")
    @Column(name = "username")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @NotBlank(message = "password is required")
    @Column(name = "password")
    private String password;

    @NotBlank(message = "email is required")
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
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true,
    fetch = FetchType.EAGER)
    private List<UserVerification> userVerifications;

    @JsonIgnore
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserConnection> following = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserConnection> follower = new HashSet<>();

    @Column(name = "follower_count")
    private Long followerCount;

    @Column(name = "following_count")
    private Long followingCount;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}
