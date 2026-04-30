package com.hashcode.shortenurl.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.InetAddress;

import static com.hashcode.shortenurl.util.Constants.GEOIP_COUNTRY_DB_CLASSPATH;
import static com.hashcode.shortenurl.util.Constants.UNKNOWN;

/**
 * Resolves country-of-origin from a client IP using the MaxMind GeoLite2 offline database.
 * <p>
 * The database file ({@code GeoLite2-Country.mmdb}) must be placed at
 * {@code src/main/resources/geoip/}. If it is absent or the IP cannot be resolved
 * (e.g. private / loopback / invalid IP), a {@link GeoLocation} with {@code "Unknown"}
 * fields is returned so that the primary redirect flow is never disrupted.
 */
@Service
public class GeoIpService {

    private static final Logger logger = LoggerFactory.getLogger(GeoIpService.class);

    private DatabaseReader databaseReader;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource(GEOIP_COUNTRY_DB_CLASSPATH);
            if (!resource.exists()) {
                logger.warn("GeoLite2 country DB not found at classpath: {}. Country lookup disabled.",
                        GEOIP_COUNTRY_DB_CLASSPATH);
                return;
            }
            try (InputStream in = resource.getInputStream()) {
                this.databaseReader = new DatabaseReader.Builder(in).build();
                logger.info("GeoLite2 country DB loaded successfully.");
            }
        } catch (Exception e) {
            logger.error("Failed to initialize GeoIP database. Country lookup disabled.", e);
        }
    }

    @PreDestroy
    public void close() {
        if (databaseReader != null) {
            try {
                databaseReader.close();
            } catch (Exception e) {
                logger.warn("Error closing GeoIP DatabaseReader: {}", e.getMessage());
            }
        }
    }

    public GeoLocation lookupCountry(String ip) {
        if (databaseReader == null || ip == null || ip.isBlank()) {
            return new GeoLocation(UNKNOWN, UNKNOWN);
        }
        try {
            InetAddress address = InetAddress.getByName(ip);
            // Short-circuit private / loopback / link-local addresses
            if (address.isAnyLocalAddress() || address.isLoopbackAddress()
                    || address.isSiteLocalAddress() || address.isLinkLocalAddress()) {
                return new GeoLocation(UNKNOWN, UNKNOWN);
            }
            CountryResponse response = databaseReader.country(address);
            String country = response.getCountry().getName();
            String code = response.getCountry().getIsoCode();
            return new GeoLocation(
                    country != null ? country : UNKNOWN,
                    code != null ? code : UNKNOWN
            );
        } catch (Exception e) {
            logger.debug("GeoIP lookup failed for ip={} : {}", ip, e.getMessage());
            return new GeoLocation(UNKNOWN, UNKNOWN);
        }
    }

    @Getter
    public static class GeoLocation {
        private final String country;
        private final String countryCode;

        public GeoLocation(String country, String countryCode) {
            this.country = country;
            this.countryCode = countryCode;
        }
    }
}

