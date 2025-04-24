package com.qt.VideoPlatformAPI.Auth;

import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Data
public class RefreshToken extends TimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserProfile userProfile;

    private String username;

    private String token;
}
