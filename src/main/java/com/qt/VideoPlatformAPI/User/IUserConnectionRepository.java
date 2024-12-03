package com.qt.VideoPlatformAPI.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IUserConnectionRepository extends JpaRepository<UserConnection, Long> {
    boolean existsByFollowerAndFollowing(UserProfile follower, UserProfile following);
    Optional<UserConnection> findByFollowerAndFollowing(UserProfile follower, UserProfile following);
    List<UserConnection> findByFollower(UserProfile follower);
}
