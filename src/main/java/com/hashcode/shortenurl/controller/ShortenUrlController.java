package com.hashcode.shortenurl.controller;

import com.hashcode.shortenurl.model.ShortenUrl;
import com.hashcode.shortenurl.service.ShortenUrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@RestController
@RequestMapping("shortenUrl")
@Getter
public class ShortenUrlController {

    private final ShortenUrlService shortenUrlService;
    private final HttpServletRequest request;

    private Logger  logger = LoggerFactory.getLogger(ShortenUrlController.class);

    public ShortenUrlController(ShortenUrlService shortenUrlService, HttpServletRequest request) {
        this.shortenUrlService = shortenUrlService;
        this.request = request;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrl> shortenUrl(@RequestBody ShortenUrl shortenUrl) {
        getLogger().info("Short URL: " + shortenUrl.getOriginalUrl());
        getLogger().info("Ip Address Of URL: " + getRequest().getRemoteAddr());
        ShortenUrl shortUrl = getShortenUrlService().createShortUrl(shortenUrl, getRequest());
        return new ResponseEntity<>(shortUrl, HttpStatus.CREATED);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> redirect(@PathVariable("shortUrl") String shortUrl) {
        getLogger().info("Redirecting URL: " + shortUrl);
        ShortenUrl url = getShortenUrlService().redirect(shortUrl, getRequest());
        // Check if the short URL exists
        if (url == null || url.getOriginalUrl() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Short URL not found");
        }
        // Validate the original URL
        try {
            URI uri = new URL(url.getOriginalUrl()).toURI(); // Ensures the URL is valid
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", String.valueOf(uri))
                    .build();
        } catch (MalformedURLException | java.net.URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid original URL");
        }
    }

    @GetMapping("/analytics/{shortUrl}")
    public ResponseEntity<ShortenUrl> getAnalytics(@PathVariable String shortUrl) {
        getLogger().info("Getting Analytics: " + shortUrl);
        ShortenUrl url = getShortenUrlService().getAnalytics(shortUrl);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping("/reportMalicious/{shortUrl}")
    public ResponseEntity<ShortenUrl> reportMalicious(@PathVariable String shortUrl) {
        getLogger().info("Getting Analytics: " + shortUrl);
        ShortenUrl url = getShortenUrlService().reportMalicious(shortUrl);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }
}
