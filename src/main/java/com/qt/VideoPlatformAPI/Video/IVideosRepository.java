package com.qt.VideoPlatformAPI.Video;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.Optional;

public interface IVideosRepository extends MongoRepository<Video, String> {
    public Optional<Video> findById(String id);
    public List<Video> findAllByUserId(Long userId);
}
