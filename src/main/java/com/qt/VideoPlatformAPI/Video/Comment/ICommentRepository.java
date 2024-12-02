package com.qt.VideoPlatformAPI.Video.Comment;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ICommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findAllByOrderByCreatedAtAsc(String videoId);
    List<Comment> findAllByOrderByCreatedAtDesc(String videoId);
    List<Comment> findAllByReplyTo(String replyTo);
}
