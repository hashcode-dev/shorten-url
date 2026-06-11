package com.hashcode.shortenurl.service.impl;

import com.hashcode.shortenurl.model.DeviceInfo;
import com.hashcode.shortenurl.model.LinkAnalytics;
import com.hashcode.shortenurl.model.ShortenUrl;
import com.hashcode.shortenurl.repository.ShortenUrlMongoRepository;
import com.hashcode.shortenurl.service.AnalyticsService;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ShortenUrlMongoRepository repository;

    public AnalyticsServiceImpl(ShortenUrlMongoRepository repository) {

        this.repository = repository;
    }

    @Override
    public LinkAnalytics getAnalytics(String shortenUrl) {

        ShortenUrl url = repository.findById(shortenUrl)
                .orElseThrow(() -> new RuntimeException("Link not found"));

        Map<String, Integer> clicksByCountry =
                url.getCountryClickMap() != null
                        ? url.getCountryClickMap()
                        : new HashMap<>();

        Map<String, Integer> clicksByDevice = new HashMap<>();

        Map<String, Integer>operatingSystems = new HashMap<>();

        Map<String, Integer>activityByHour = new HashMap<>();

        Map<String, Integer>clickActivityOverTime = new HashMap<>();



        if (url.getDeviceInfoList() != null) {

            for (DeviceInfo device : url.getDeviceInfoList()) {

                String deviceType =
                        device.getDeviceType() != null
                                ? device.getDeviceType()
                                : "Unknown";

                operatingSystems.put(
                        deviceType,
                        operatingSystems.getOrDefault(deviceType, 0) + 1
                );

                if(device.getAccessedAt() != null) {
                    String hour = String.valueOf(device.getAccessedAt().getHour());
                    activityByHour.put(
                            hour,
                            activityByHour.getOrDefault(hour, 0) + 1
                    );

                    String date = device.getAccessedAt().toLocalDate().toString();
                    clickActivityOverTime.put(
                            date,
                            clickActivityOverTime.getOrDefault(date, 0) + 1
                    );
                }
            }

        }

        return new LinkAnalytics(
                url.getShortUrl(),
                url.getOriginalUrl(),
                url.getClickCount(),
                clicksByCountry,
                clicksByDevice,
                new HashMap<>(), // browser
                operatingSystems,
                activityByHour,
                clickActivityOverTime // timeline
        );
    }
}