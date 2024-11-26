package com.qt.VideoPlatformAPI.Playlist;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPlaylistRepository extends MongoRepository<Playlist, String> {
}
