package com.qt.VideoPlatformAPI.Video.View;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IViewHistoryRepository extends MongoRepository<ViewHistory, String> {
    Slice<ViewHistory> findByViewerUsername(String viewerUsername, Pageable pageable);
}
