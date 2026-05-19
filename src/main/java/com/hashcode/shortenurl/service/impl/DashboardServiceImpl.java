package com.hashcode.shortenurl.service.impl;

import com.hashcode.shortenurl.model.DashboardAnalytics;
import com.hashcode.shortenurl.model.ShortenUrl;
import com.hashcode.shortenurl.repository.ShortenUrlMongoRepository;
import com.hashcode.shortenurl.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ShortenUrlMongoRepository repository;

    public DashboardServiceImpl(ShortenUrlMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public DashboardAnalytics getDashboardData() {

        List<ShortenUrl> allUrls = repository.findAll();

        long totalLinksCreated = allUrls.size();
        long activeLinks = repository.countByActiveTrue();
        long allTimeClicks = allUrls.stream().mapToLong(ShortenUrl::getClickCount).sum();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long totalClicksToday = repository.findByLastClickedAtAfter(startOfDay).stream().mapToLong(ShortenUrl::getClickCount).sum();
        List<ShortenUrl> topPerformingLinks = repository.findTop5ByOrderByClickCountDesc();
        Map<String, Integer> clicksByDevice = new HashMap<>();
        Map<String, Integer> clicksByCountry = new HashMap<>();

        for (ShortenUrl url : allUrls) {
            String device = url.getDevice() != null ? url.getDevice() : "Unknown";
            clicksByDevice.put(device, clicksByDevice.getOrDefault(device, 0) + 1);


            String country = url.getCountry() != null ? url.getCountry() : "Unknown";
            clicksByCountry.put(country, clicksByCountry.getOrDefault(country, 0) + 1);
        }

        return new DashboardAnalytics(totalLinksCreated, totalClicksToday, allTimeClicks, activeLinks, topPerformingLinks, clicksByDevice, clicksByCountry);
    }
}