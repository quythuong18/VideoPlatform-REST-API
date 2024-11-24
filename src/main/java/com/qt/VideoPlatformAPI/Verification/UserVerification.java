package com.qt.VideoPlatformAPI.Verification;

import com.qt.VideoPlatformAPI.User.UserProfile;
import com.qt.VideoPlatformAPI.Utils.TimeAudit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserVerification extends TimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "fk_user_profile_id")
    private UserProfile user;

    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "expiration_time")
    private Instant expirationTime;
}
