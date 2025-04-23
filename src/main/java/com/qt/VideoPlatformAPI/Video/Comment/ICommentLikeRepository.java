package com.qt.VideoPlatformAPI.Video.Comment;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ICommentLikeRepository extends MongoRepository<CommentLike, String> {
    Optional<CommentLike> findByCommentIdAndUserId(ObjectId commentId, Long userId);
}
