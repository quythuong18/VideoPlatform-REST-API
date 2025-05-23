package com.qt.VideoPlatformAPI.Video.Like;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ILikeRepository extends MongoRepository<VideoLike, String> {
    public Optional<VideoLike> findByVideoIdAndUserId(ObjectId videoId, Long userId);
    public List<VideoLike> findByUserId(Long userId, Pageable pageable);
    public void deleteAllByVideoId(String videoId);
}
