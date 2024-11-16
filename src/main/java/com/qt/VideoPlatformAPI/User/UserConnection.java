package com.qt.VideoPlatformAPI.User;

import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class UserConnection extends TimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private UserProfile follower;

    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private UserProfile following;
}
