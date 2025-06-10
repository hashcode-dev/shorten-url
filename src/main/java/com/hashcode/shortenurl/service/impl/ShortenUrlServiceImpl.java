package com.hashcode.shortenurl.service.impl;

import com.hashcode.shortenurl.model.ShortUrl;
import com.hashcode.shortenurl.repository.ShortenUrlMongoRepository;
import com.hashcode.shortenurl.service.ShortenUrlService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
public class ShortenUrlServiceImpl implements ShortenUrlService {
    @Autowired
    ShortenUrlMongoRepository shortenUrlMongoRepository;

    @Override
    public ShortUrl shortenUrl(ShortUrl shortenUrl) {
        return getShortenUrlMongoRepository().save(shortenUrl);
    }

    @Override
    public ShortUrl findByShortUrl(String shortUrl) {
        return getShortenUrlMongoRepository().findById(shortUrl).orElse(null);
    }
}
