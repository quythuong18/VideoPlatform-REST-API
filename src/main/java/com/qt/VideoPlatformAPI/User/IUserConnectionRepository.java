package com.qt.VideoPlatformAPI.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IUserConnectionRepository extends JpaRepository<UserConnection, Long> {
    boolean existsByFollowerAndFollowing(UserProfile follower, UserProfile following);
    Optional<UserConnection> findByFollowerAndFollowing(UserProfile follower, UserProfile following);
    Slice<UserConnection> findByFollower(UserProfile follower, Pageable pageable);
    Slice<UserConnection> findByFollowing(UserProfile following, Pageable pageable);
}
