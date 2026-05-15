package com.hashcode.shortenurl.repository;

import com.hashcode.shortenurl.model.ShortenUrl;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShortenUrlMongoRepository extends MongoRepository<ShortenUrl, String> {

    long countByStatus(String status);

    @Aggregation(pipeline = {
            "{ $group: { _id: null, total: { $sum: \"$clicks\" } } }"
            "{ $project: { _id: 0, total: 1 } }"
    })
    long getTotalClicks();
}