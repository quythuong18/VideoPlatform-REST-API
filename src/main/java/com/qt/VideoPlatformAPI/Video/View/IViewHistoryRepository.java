package com.qt.VideoPlatformAPI.Video.View;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IViewHistoryRepository extends MongoRepository<ViewHistory, String> {
}
