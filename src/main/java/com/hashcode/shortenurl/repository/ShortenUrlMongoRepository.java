package com.hashcode.shortenurl.repository;

import com.hashcode.shortenurl.model.ShortenUrl;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ShortenUrlMongoRepository extends MongoRepository<ShortenUrl, String> {

    long countByActiveTrue();

    List<ShortenUrl> findByLastClickedAtAfter(LocalDateTime time);

    List<ShortenUrl> findTop5ByOrderByClickCountDesc();
}