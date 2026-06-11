package com.hashcode.shortenurl.controller;

import com.hashcode.shortenurl.model.LinkAnalytics;
import com.hashcode.shortenurl.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<LinkAnalytics> getAnalytics(
            @PathVariable String shortUrl) {

        return ResponseEntity.ok(
                analyticsService.getAnalytics(shortUrl)
        );
    }
}