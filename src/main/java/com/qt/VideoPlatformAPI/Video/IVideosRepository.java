package com.qt.VideoPlatformAPI.Video;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

public interface IVideosRepository extends MongoRepository<Video, Long> {
    public Optional<Video> findById(String id);
}
