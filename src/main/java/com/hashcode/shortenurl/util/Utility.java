package com.hashcode.shortenurl.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.apache.commons.validator.routines.DomainValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
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
}
