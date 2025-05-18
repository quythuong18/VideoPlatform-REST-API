package com.qt.VideoPlatformAPI.Video;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class CustomVideoRepository {
  private MongoTemplate mongoTemplate;

    public List<Video> getRandomVideos(Integer count) {
        // Criteria
        Criteria criteria = Criteria.where("isUploaded").is(true).and("isProcessed").is(true);

        // Create an aggregation pipeline with $sample stage
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sample(count)
        );

        // Execute the aggregation query on the "video" collection
        AggregationResults<Video> results = mongoTemplate.aggregate(aggregation, "videos", Video.class);

        return results.getMappedResults();
    }

    public List<Video> searchByTitle(String searchPattern, Integer count) {
        Query query = new Query();
        // Create criteria to search for the substring in the specific field
        query.addCriteria(Criteria.where("title").regex(searchPattern, "i")); // Case-insensitive regex

        query.addCriteria(Criteria.where("isPrivate").is(false));
        query.addCriteria(Criteria.where("isUploaded").is(true));
        query.addCriteria(Criteria.where("isProcessed").is(true));

        query.limit(count);

        // Use count() to get the number of matching documents
        return mongoTemplate.find(query, Video.class, "videos");
    }
    public List<Video> searchByTag(String tag, Integer count) {
        Query query = new Query();

        // Search for videos where "tags" array contains the specified tag
        query.addCriteria(Criteria.where("tags").in(tag));

        query.addCriteria(Criteria.where("isPrivate").is(false));
        query.addCriteria(Criteria.where("isUploaded").is(true));
        query.addCriteria(Criteria.where("isProcessed").is(true));

        query.limit(count);

        // Execute the query and return the results
        return mongoTemplate.find(query, Video.class, "videos");
    }
}
