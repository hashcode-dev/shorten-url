package com.hashcode.shortenurl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {
    private String deviceType;
    private String osType;
    private String ipAddress;
    private String country;
    private String countryCode;
    private LocalDateTime accessedAt;
}

