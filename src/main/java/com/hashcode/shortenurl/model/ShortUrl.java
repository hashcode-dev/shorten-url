package com.hashcode.shortenurl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document("short_url")
@AllArgsConstructor
public class ShortUrl {
    @Id
    private String shortUrl;
    private String originalUrl;
    private LocalDateTime createdAt;
    private long clickCount;
}
