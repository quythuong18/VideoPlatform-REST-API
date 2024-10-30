package com.qt.VideoPlatformAPI.Video;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

public interface IVideosRepository extends MongoRepository<Video, Long> {
}
