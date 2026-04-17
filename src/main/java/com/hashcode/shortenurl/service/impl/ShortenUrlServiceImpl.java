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
    final GeoIpService geoIpService;
    private Logger logger = LoggerFactory.getLogger(ShortenUrlServiceImpl.class);

    public ShortenUrlServiceImpl(ShortenUrlMongoRepository shortenUrlMongoRepository, GeoIpService geoIpService) {
        this.shortenUrlMongoRepository = shortenUrlMongoRepository;
        this.geoIpService = geoIpService;
    }

    @Override
    public ShortenUrl createShortUrl(ShortenUrl shortenUrl, HttpServletRequest request) {

        if (shortenUrl.getOriginalUrl() != null && !shortenUrl.getOriginalUrl().trim().isEmpty()) {
            // Normalize URL by adding protocol if missing
            String normalizedUrl = normalizeUrl(shortenUrl.getOriginalUrl());
            String shortUrl = generateShortUrl();

            while (getShortenUrlMongoRepository().existsById(shortUrl)) {
                shortUrl = generateShortUrl();
            }

            ShortenUrl url = new ShortenUrl();
            Map<String, Integer> map = new HashMap<>();
            map.put(getClientIp(request), 0);
            if(shortenUrl.getAlias() != null && !shortenUrl.getAlias().trim().isEmpty()) {
                if(!getShortenUrlMongoRepository().existsById(shortenUrl.getAlias().trim())) {
                    url.setShortUrl(shortenUrl.getAlias().trim());
                    url.setAlias(shortenUrl.getAlias().trim());
                } else {
                    throw new IllegalArgumentException("Alias already exists");
                }
            } else {
                url.setShortUrl(shortUrl);
            }
            url.setOriginalUrl(normalizedUrl);
            url.setCreatedAt(LocalDateTime.now());
            url.setClickCount(0);
            url.setIpAddressMap(map);

            return getShortenUrlMongoRepository().save(url);
        } else {
            throw new IllegalArgumentException("URL cannot be empty");
        }
    }

    @Override
    public ShortenUrl redirect(String shortUrl, HttpServletRequest request) {
        ShortenUrl shortenUrl = getShortenUrlMongoRepository().findById(shortUrl).orElseThrow(() -> new RuntimeException("Short URL not found"));
        shortenUrl.setClickCount(shortenUrl.getClickCount() + 1);

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
        return getShortenUrlMongoRepository().findAll();
    }
}
