package com.qt.VideoPlatformAPI.Video;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class CustomVideoRepository {
  private MongoTemplate mongoTemplate;

    public List<Video> getRandomVideos(Integer count) {
        // Create an aggregation pipeline with $sample stage
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.sample(count));

        // Execute the aggregation query on the "video" collection
        AggregationResults<Video> results = mongoTemplate.aggregate(aggregation, "video", Video.class);

        return results.getMappedResults();
    }
}
