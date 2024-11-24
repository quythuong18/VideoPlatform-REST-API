package com.qt.VideoPlatformAPI.Video.Like;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ILikeRepository extends MongoRepository<VideoLike, String> {
    public Optional<VideoLike> findByVideoIdAndUserId(String videoId, Long UserId);
}
