package com.hashcode.shortenurl.repository;

import com.hashcode.shortenurl.model.ShortUrl;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShortenUrlMongoRepository extends MongoRepository<ShortUrl, String> {
}
