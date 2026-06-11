package com.hashcode.shortenurl.service;

import com.hashcode.shortenurl.model.LinkAnalytics;

public interface AnalyticsService {

    LinkAnalytics getAnalytics(String shortUrl);
}