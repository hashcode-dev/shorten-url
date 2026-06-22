package com.hashcode.shortenurl.model;

import  lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LinkListItem {

    private String  shortUrl;
    private String originalUrl;
    private long clickCount;
    private LocalDateTime createdAt;
    private boolean active;
}
