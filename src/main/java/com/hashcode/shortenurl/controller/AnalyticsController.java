package com.hashcode.shortenurl.controller;

import com.hashcode.shortenurl.model.LinkAnalytics;
import com.hashcode.shortenurl.service.AnalyticsService;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Getter
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<LinkAnalytics> getAnalytics(
            @PathVariable String shortUrl) {

        return ResponseEntity.ok(
                getAnalyticsService().getAnalytics(shortUrl)
        );
    }
}