package com.hashcode.shortenurl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalytics{

    private long totalLinksCreated;
    private long totalClicksToday;
    private long allTimeClicks;
    private long activeLinks;
    private List<ShortenUrl> topPerformingLinks;
    private Map<String, Integer> clicksByDevice;
    private Map<String, Integer> clicksByCountry;
}