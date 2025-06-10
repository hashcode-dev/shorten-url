package com.hashcode.shortenurl.service;

import com.hashcode.shortenurl.model.ShortUrl;

public interface ShortenUrlService {
    public ShortUrl shortenUrl(ShortUrl shortenUrl);
    public ShortUrl findByShortUrl(String shortUrl);
}
