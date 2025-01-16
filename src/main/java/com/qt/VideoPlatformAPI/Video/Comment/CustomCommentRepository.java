package com.qt.VideoPlatformAPI.Video.Comment;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class CustomCommentRepository {
    private MongoTemplate mongoTemplate;

    public List<Comment> getAllParentComment(String videoId, Integer page, Integer size) {
        Query query = new Query();

        query.addCriteria(Criteria.where("videoId").is(new ObjectId(videoId)));
        query.addCriteria(Criteria.where("replyTo").is(null));

        query.skip((long) page * size);
        query.limit(size);

        return mongoTemplate.find(query, Comment.class, "comment");
    }

    public List<Comment> getAllChildrenComment(String commentId, Integer page, Integer size) {
        Query query = new Query();

        query.addCriteria(Criteria.where("replyTo").is(new ObjectId(commentId)));

        query.skip((long) page * size);
        query.limit(size);

        return mongoTemplate.find(query, Comment.class, "comment");
    }
}
