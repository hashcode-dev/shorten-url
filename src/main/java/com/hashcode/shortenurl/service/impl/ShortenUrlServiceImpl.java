package com.hashcode.shortenurl.service.impl;

import com.hashcode.shortenurl.model.DeviceInfo;
import com.hashcode.shortenurl.model.ShortenUrl;
import com.hashcode.shortenurl.repository.ShortenUrlMongoRepository;
import com.hashcode.shortenurl.service.GeoIpService;
import com.hashcode.shortenurl.service.ShortenUrlService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hashcode.shortenurl.util.Utility.*;

@Service
@Getter
public class ShortenUrlServiceImpl implements ShortenUrlService {

    final ShortenUrlMongoRepository shortenUrlMongoRepository;


    private final Logger logger = LoggerFactory.getLogger(ShortenUrlServiceImpl.class);
    final GeoIpService geoIpService;



    public ShortenUrlServiceImpl(ShortenUrlMongoRepository shortenUrlMongoRepository, GeoIpService geoIpService) {
        this.shortenUrlMongoRepository = shortenUrlMongoRepository;
        this.geoIpService = geoIpService;
    }

    @Override
    public ShortenUrl createShortUrl(ShortenUrl shortenUrl, HttpServletRequest request) {

        if (shortenUrl.getOriginalUrl() == null || shortenUrl.getOriginalUrl().trim().isEmpty()) {

            throw new IllegalArgumentException("URL cannot be empty");
        }

        String normalizedUrl = normalizeUrl(shortenUrl.getOriginalUrl());

        String generatedShortUrl = generateShortUrl();

        while (getShortenUrlMongoRepository().existsById(generatedShortUrl)) {

            generatedShortUrl = generateShortUrl();
        }

        ShortenUrl url = new ShortenUrl();
        Map<String, Integer> map = new HashMap<>();
        map.put(getClientIp(request), 0);


        if (shortenUrl.getAlias() != null && !shortenUrl.getAlias().trim().isEmpty()) {

            String customAlias = shortenUrl.getAlias().trim();

            if (getShortenUrlMongoRepository().existsById(customAlias)) {

                throw new IllegalArgumentException("Alias already exists");
            }

            url.setShortUrl(customAlias);
            url.setAlias(customAlias);

        } else {
            url.setShortUrl(generatedShortUrl);
            url.setAlias(generatedShortUrl);
        }

        url.setOriginalUrl(normalizedUrl);
        url.setCreatedAt(LocalDateTime.now());
        url.setLastClickedAt(LocalDateTime.now());
        url.setActive(true);
        url.setClickCount(0);
        url.setIpAddressMap(map);

        logger.info("Short URL created: {}", url.getShortUrl());
        return getShortenUrlMongoRepository().save(url);
    }

    @Override
    public ShortenUrl redirect(String shortUrl, HttpServletRequest request) {

        ShortenUrl shortenUrl = shortenUrlMongoRepository
                .findById(shortUrl)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        shortenUrl.setClickCount(shortenUrl.getClickCount() + 1);


        // set active
        shortenUrl.setActive(true);

        // last clicked time
        shortenUrl.setLastClickedAt(LocalDateTime.now());

        // device
        String userAgent = request.getHeader("User-Agent");

        if (userAgent != null && userAgent.contains("Mobile")) {
            shortenUrl.setDevice("Mobile");
        } else {
            shortenUrl.setDevice("Desktop");
        }

        // country
        shortenUrl.setCountry("India");

        // ip tracking
        String ip = getClientIp(request);

        Map<String, Integer> ipMap = shortenUrl.getIpAddressMap();

        if (ipMap == null) {
            ipMap = new HashMap<>();
        }

        ipMap.put(ip, ipMap.getOrDefault(ip, 0) + 1);

        shortenUrl.setIpAddressMap(ipMap);

        String clientIp = getClientIp(request);

        if (shortenUrl.getIpAddressMap() == null) {
            shortenUrl.setIpAddressMap(new HashMap<>());
        }
        shortenUrl.getIpAddressMap().merge(clientIp, 1, Integer::sum);

        // Resolve country of origin for the click (graceful fallback to "Unknown")
        GeoIpService.GeoLocation geo = getGeoIpService().lookupCountry(clientIp);
        if (shortenUrl.getCountryClickMap() == null) {
            shortenUrl.setCountryClickMap(new HashMap<>());
        }
        shortenUrl.getCountryClickMap().merge(geo.getCountry(), 1, Integer::sum);

        // Capture device type, OS type and country from the incoming request
        DeviceInfo deviceInfo = extractDeviceInfo(request, geo.getCountry(), geo.getCountryCode());
        if (shortenUrl.getDeviceInfoList() == null) {
            shortenUrl.setDeviceInfoList(new ArrayList<>());
        }
        shortenUrl.getDeviceInfoList().add(deviceInfo);

        return getShortenUrlMongoRepository().save(shortenUrl);

    }


    @Override
    public ShortenUrl getAnalytics(String shortUrl) {

        return getShortenUrlMongoRepository().findById(shortUrl).orElseThrow(() -> new RuntimeException("Short URL not found"));
    }

    @Override
    public Map<String, Integer> getCountryAnalytics(String shortUrl) {
        ShortenUrl url = getShortenUrlMongoRepository().findById(shortUrl)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));
        return url.getCountryClickMap() != null ? url.getCountryClickMap() : new HashMap<>();
    }

    @Override
    public ShortenUrl reportMalicious(String shortUrl) {

        ShortenUrl shortenUrl = getShortenUrlMongoRepository().findById(shortUrl).orElseThrow(() -> new RuntimeException("Short URL not found"));
        return getShortenUrlMongoRepository().save(shortenUrl);
    }

    @Override
    public List<ShortenUrl> getAllShortUrls() {

        return getShortenUrlMongoRepository()
                .findAll();
    }
}