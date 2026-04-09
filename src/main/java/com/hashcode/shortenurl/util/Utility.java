package com.hashcode.shortenurl.util;

import com.hashcode.shortenurl.model.DeviceInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.apache.commons.validator.routines.DomainValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Random;

import static com.hashcode.shortenurl.util.Constants.*;

@Getter
public class Utility {

    private Logger logger = LoggerFactory.getLogger(Utility.class);

    public static String normalizeUrl(String originalUrl) {
        String trimmedUrl = originalUrl.trim();
        if (!trimmedUrl.startsWith(HTTP_PROTOCOL) && !trimmedUrl.startsWith(DEFAULT_PROTOCOL)) {
            return DEFAULT_PROTOCOL + trimmedUrl; // Default to HTTPS
        }
        // Validate URL format
        URL url;
        try {
            url = new URL(trimmedUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format: " + originalUrl);
        }

        // Validate domain
        String domain = url.getHost();
        DomainValidator domainValidator = DomainValidator.getInstance(true); // Allow local TLDs for testing
        if (!domainValidator.isValid(domain)) {
            throw new IllegalArgumentException("Invalid domain: " + domain);
        }
        return trimmedUrl;
    }

    public static String generateShortUrl() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            sb.append(BASE62.charAt(random.nextInt(BASE62.length())));
        }
        return sb.toString();
    }

    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For may contain a comma-separated list; take the first IP
            remoteAddr = xForwardedFor.split(",")[0].trim();
        }
        return remoteAddr;
    }

    public static DeviceInfo extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String deviceType = "Unknown";
        String osType = "Unknown";

        if (userAgent != null && !userAgent.isEmpty()) {
            osType = extractOsType(userAgent);
            deviceType = extractDeviceType(userAgent);
        }

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceType(deviceType);
        deviceInfo.setOsType(osType);
        deviceInfo.setIpAddress(getClientIp(request));
        deviceInfo.setAccessedAt(LocalDateTime.now());
        return deviceInfo;
    }

    private static String extractOsType(String userAgent) {
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows nt 10")) {
            return "Windows 10";
        } else if (ua.contains("windows nt 11") || (ua.contains("windows nt 10") && ua.contains("win64"))) {
            return "Windows 11";
        } else if (ua.contains("windows nt 6.3")) {
            return "Windows 8.1";
        } else if (ua.contains("windows nt 6.2")) {
            return "Windows 8";
        } else if (ua.contains("windows nt 6.1")) {
            return "Windows 7";
        } else if (ua.contains("windows")) {
            return "Windows";
        } else if (ua.contains("mac os x")) {
            return "macOS";
        } else if (ua.contains("iphone os") || ua.contains("ipad")) {
            // Extract iOS version
            int idx = ua.indexOf("os ");
            if (idx != -1) {
                String sub = ua.substring(idx + 3);
                StringBuilder version = new StringBuilder("iOS ");
                for (char c : sub.toCharArray()) {
                    if (Character.isDigit(c) || c == '_' || c == '.') {
                        version.append(c == '_' ? '.' : c);
                    } else if (version.length() > 4) {
                        break;
                    }
                }
                return version.toString().trim();
            }
            return "iOS";
        } else if (ua.contains("android")) {
            int idx = ua.indexOf("android ");
            if (idx != -1) {
                String sub = ua.substring(idx + 8);
                StringBuilder version = new StringBuilder("Android ");
                for (char c : sub.toCharArray()) {
                    if (Character.isDigit(c) || c == '.') {
                        version.append(c);
                    } else {
                        break;
                    }
                }
                return version.toString().trim();
            }
            return "Android";
        } else if (ua.contains("linux")) {
            return "Linux";
        } else if (ua.contains("cros")) {
            return "Chrome OS";
        }
        return "Unknown";
    }

    private static String extractDeviceType(String userAgent) {
        String ua = userAgent.toLowerCase();
        if (ua.contains("bot") || ua.contains("crawler") || ua.contains("spider")) {
            return "Bot";
        } else if (ua.contains("mobile") || ua.contains("iphone") || (ua.contains("android") && !ua.contains("tablet"))) {
            return "Mobile";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "Tablet";
        } else if (ua.contains("windows") || ua.contains("macintosh") || ua.contains("linux") || ua.contains("cros")) {
            return "Desktop";
        }
        return "Unknown";
    }
}
