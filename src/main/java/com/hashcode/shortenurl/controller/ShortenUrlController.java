package com.hashcode.shortenurl.controller;

import com.hashcode.shortenurl.model.ShortUrl;
import com.hashcode.shortenurl.service.ShortenUrlService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> redirect(@PathVariable String shortUrl) {
        ShortUrl url = getShortenUrlService().findByShortUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url.getOriginalUrl())
                .build();
    }
}
