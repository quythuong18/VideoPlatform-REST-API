package com.qt.VideoPlatformAPI.Video.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ICommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findAllByVideoIdOrderByCreatedAtAsc(String videoId);
    List<Comment> findAllByVideoIdOrderByCreatedAtDesc(String videoId);
    List<Comment> findAllByReplyToOrderByCreatedAtAsc(String replyTo);
    List<Comment> findAllByReplyToOrderByCreatedAtDesc(String replyTo);
    List<Comment> findAllByUserId(Long userId);
    Optional<Comment> findById(String id);

    @Aggregation(pipeline = {
            "{ $lookup: { " +
                    "'from': 'video', " +
                    "'let': { 'videoId': '$videoId' }, " +
                    "'pipeline': [ " +
                    "{ $addFields: { 'idString': { $toString: '$_id' } } }, " +
                    "{ $match: { $expr: { $eq: [ '$idString', '$$videoId' ] } } } " +
                    "], " +
                    "'as': 'videoDetails' " +
                    "}}",
            "{ $unwind: { 'path': '$videoDetails' } }",
            "{ $match: { 'videoDetails.userId': ?0 } }"
    })
    Slice<Comment> findAllCommentForAllVideosByUserId(Long userId, Pageable pageable);
}
