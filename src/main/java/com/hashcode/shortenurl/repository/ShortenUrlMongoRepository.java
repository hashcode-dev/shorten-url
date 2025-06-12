package com.hashcode.shortenurl.repository;

import com.hashcode.shortenurl.model.ShortenUrl;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShortenUrlMongoRepository extends MongoRepository<ShortenUrl, String> {
}
