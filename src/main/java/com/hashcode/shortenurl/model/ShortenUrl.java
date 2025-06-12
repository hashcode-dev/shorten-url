package com.hashcode.shortenurl.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Document("short_url")
@NoArgsConstructor
public class ShortenUrl {
    @Id
    private String shortUrl;
    private String alias;
    private String originalUrl;
    private LocalDateTime createdAt;
    private long clickCount;
    private Map<String, Integer> ipAddressMap;
}
