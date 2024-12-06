package com.qt.VideoPlatformAPI.Video.Comment;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ICommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findAllByVideoIdOrderByCreatedAtAsc(String videoId);
    List<Comment> findAllByVideoIdOrderByCreatedAtDesc(String videoId);
    List<Comment> findAllByUserIdOrderByCreatedAtAsc(String userId);
    List<Comment> findAllByUserIdOrderByCreatedAtDesc(String userId);
    List<Comment> findAllByReplyTo(String replyTo);
}
