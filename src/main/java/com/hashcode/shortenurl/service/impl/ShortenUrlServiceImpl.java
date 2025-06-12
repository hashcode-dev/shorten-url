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
import java.util.Random;

@Service
@Getter
public class ShortenUrlServiceImpl implements ShortenUrlService {

    final ShortenUrlMongoRepository shortenUrlMongoRepository;
    private Logger logger = LoggerFactory.getLogger(ShortenUrlServiceImpl.class);

    public ShortenUrlServiceImpl(ShortenUrlMongoRepository shortenUrlMongoRepository) {
        this.shortenUrlMongoRepository = shortenUrlMongoRepository;
    }

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_URL_LENGTH = 7;

    @Override
    public ShortenUrl createShortUrl(ShortenUrl shortenUrl) {
        String shortUrl = generateShortUrl();

        while (getShortenUrlMongoRepository().existsById(shortUrl)) {
            shortUrl = generateShortUrl();
        }

        ShortenUrl url = new ShortenUrl();
        url.setShortUrl(shortUrl);
        url.setOriginalUrl(shortenUrl.getOriginalUrl());
        url.setCreatedAt(LocalDateTime.now());
        url.setClickCount(0);
        url.setIpAddressList(Collections.emptyList());

        return getShortenUrlMongoRepository().save(url);
    }

    @Override
    public ShortenUrl redirect(String shortUrl, HttpServletRequest request) {
        ShortenUrl shortenUrl = getShortenUrlMongoRepository().findById(shortUrl).orElseThrow(() -> new RuntimeException("Short URL not found"));
        shortenUrl.setClickCount(shortenUrl.getClickCount() + 1);
        shortenUrl.getIpAddressList().add(getClientIp(request));
        return getShortenUrlMongoRepository().save(shortenUrl);
    }

    @Override
    public ShortenUrl getAnalytics(String shortUrl) {
        return getShortenUrlMongoRepository().findById(shortUrl).orElseThrow(() -> new RuntimeException("Short URL not found"));
    }

    private String generateShortUrl() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            sb.append(BASE62.charAt(random.nextInt(BASE62.length())));
        }
        return sb.toString();
    }

    private String getClientIp(HttpServletRequest request) {
        getLogger().info("Client IP Address");
        String remoteAddr = request.getRemoteAddr();
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For may contain a comma-separated list; take the first IP
            remoteAddr = xForwardedFor.split(",")[0].trim();
        }
        return remoteAddr;
    }
}
