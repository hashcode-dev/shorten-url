package com.hashcode.shortenurl.controller;

import com.hashcode.shortenurl.model.ShortUrl;
import com.hashcode.shortenurl.service.ShortenUrlService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    ShortenUrlService shortenUrlService;


    @PostMapping("/shorten")
    public ResponseEntity<ShortUrl> shortenUrl(@RequestBody ShortUrl originalUrl) {
        ShortUrl shortUrl = getShortenUrlService().shortenUrl(originalUrl);
        return new ResponseEntity<>(shortUrl, HttpStatus.CREATED);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> redirect(@PathVariable("shortUrl") String shortUrl) {
        ShortUrl url = getShortenUrlService().findByShortUrl(shortUrl);
        System.out.println("shortUrl: " + shortUrl);
        // Check if the short URL exists
        if (url == null || url.getOriginalUrl() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Short URL not found");
        }
        // Validate the original URL
        try {
            URI uri = new URL("https://" + url.getOriginalUrl()).toURI(); // Ensures the URL is valid
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", String.valueOf(uri))
                    .build();
        } catch (MalformedURLException | java.net.URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid original URL");
        }
    }
}
