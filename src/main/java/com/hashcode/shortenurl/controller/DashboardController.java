package com.hashcode.shortenurl.controller;

import com.hashcode.shortenurl.model.DashboardAnalytics;
import com.hashcode.shortenurl.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardAnalytics> getDashboardSummary() {

        return ResponseEntity.ok(
                dashboardService.getDashboardData()
        );
    }
}