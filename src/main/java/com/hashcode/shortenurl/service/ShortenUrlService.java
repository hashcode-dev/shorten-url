package com.hashcode.shortenurl.service;

import com.hashcode.shortenurl.model.ShortenUrl;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ShortenUrlService {
    public ShortenUrl createShortUrl(ShortenUrl shortenUrl, HttpServletRequest request);
    public ShortenUrl redirect(String shortUrl, HttpServletRequest request);
    public ShortenUrl getAnalytics(String shortUrl);
    public ShortenUrl reportMalicious(String shortUrl);
    public List<ShortenUrl> getAllShortUrls();
}
