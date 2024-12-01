package com.qt.VideoPlatformAPI.Playlist;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IPlaylistRepository extends MongoRepository<Playlist, String> {
    List<Playlist> findAllByUserId(Long userId);
}
