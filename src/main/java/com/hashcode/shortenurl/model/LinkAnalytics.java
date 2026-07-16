package com.hashcode.shortenurl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkAnalytics {

    private String shortUrl;
    private String originalUrl;
    private long totalClicks;

    private Map<String, Integer> clicksByCountry;
    private Map<String, Integer> clicksByDevice;
    private Map<String, Integer> clicksByBrowser;
    private Map<String, Integer> operatingSystems;
    private Map<String, Integer> activityByHour;
    private Map<String, Integer> clickActivityOverTime;
}

