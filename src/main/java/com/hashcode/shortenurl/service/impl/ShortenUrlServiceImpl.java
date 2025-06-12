package com.hashcode.shortenurl.service.impl;

import com.hashcode.shortenurl.model.ShortenUrl;
import com.hashcode.shortenurl.repository.ShortenUrlMongoRepository;
import com.hashcode.shortenurl.service.ShortenUrlService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.hashcode.shortenurl.util.Utility.*;

@Service
@Getter
public class ShortenUrlServiceImpl implements ShortenUrlService {

    final ShortenUrlMongoRepository shortenUrlMongoRepository;
    private Logger logger = LoggerFactory.getLogger(ShortenUrlServiceImpl.class);

    public ShortenUrlServiceImpl(ShortenUrlMongoRepository shortenUrlMongoRepository) {
        this.shortenUrlMongoRepository = shortenUrlMongoRepository;
    }

    @Override
    public ShortenUrl createShortUrl(ShortenUrl shortenUrl, HttpServletRequest request) {

        if (shortenUrl.getOriginalUrl() != null && !shortenUrl.getOriginalUrl().trim().isEmpty()) {
            // Normalize URL by adding protocol if missing
            String normalizedUrl = normalizeUrl(shortenUrl.getOriginalUrl());
            String shortUrl = generateShortUrl();

            while (getShortenUrlMongoRepository().existsById(shortUrl)) {
                shortUrl = generateShortUrl();
            }

            ShortenUrl url = new ShortenUrl();
            Map<String, Integer> map = new HashMap<>();
            map.put(getClientIp(request), 0);
            if(shortenUrl.getAlias() != null && !shortenUrl.getAlias().trim().isEmpty()) {
                if(!getShortenUrlMongoRepository().existsById(shortenUrl.getAlias().trim())) {
                    url.setShortUrl(shortenUrl.getAlias().trim());
                    url.setAlias(shortenUrl.getAlias().trim());
                } else {
                    throw new IllegalArgumentException("Alias already exists");
                }
            } else {
                url.setShortUrl(shortUrl);
            }
            url.setOriginalUrl(normalizedUrl);
            url.setCreatedAt(LocalDateTime.now());
            url.setClickCount(0);
            url.setIpAddressMap(map);

            return getShortenUrlMongoRepository().save(url);
        } else {
            throw new IllegalArgumentException("URL cannot be empty");
        }
    }

    @Override
    public ShortenUrl redirect(String shortUrl, HttpServletRequest request) {
        ShortenUrl shortenUrl = getShortenUrlMongoRepository().findById(shortUrl).orElseThrow(() -> new RuntimeException("Short URL not found"));
        shortenUrl.setClickCount(shortenUrl.getClickCount() + 1);
        shortenUrl.getIpAddressMap().put(getClientIp(request), shortenUrl.getIpAddressMap().get(getClientIp(request)) + 1);
        return getShortenUrlMongoRepository().save(shortenUrl);
    }

    @Override
    public ShortenUrl getAnalytics(String shortUrl) {
        return getShortenUrlMongoRepository().findById(shortUrl).orElseThrow(() -> new RuntimeException("Short URL not found"));
    }
}
