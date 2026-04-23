package com.tp.main.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tp.main.Utils.Base62;
import com.tp.main.dto.ClientInfo;
import com.tp.main.dto.SafeBrowsingRequest;
import com.tp.main.dto.SafeBrowsingResponse;
import com.tp.main.dto.ThreatEntry;
import com.tp.main.dto.ThreatInfo;
import com.tp.main.entities.UrlMapping;
import com.tp.main.externalservices.SafeBrowseringClient;
import com.tp.main.repository.UrlRepository;

@Service
public class UrlServiceImpl {

	@Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UserBuffer userBuffer;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private SafeBrowseringClient safeBrowsingClient;

    private static final String REDIS_PREFIX = "url:";
    
    @Value("${google.api.key}")
    private String googleAPIKey;
    
    
    
    private boolean isUnsafe(String url) {
        ThreatInfo threatInfo = new ThreatInfo(
            List.of("MALWARE", "SOCIAL_ENGINEERING"), 
            List.of("ANY_PLATFORM"), 
            List.of("URL"), 
            List.of(new ThreatEntry(url))
        );
        
        SafeBrowsingRequest request = new SafeBrowsingRequest(
                new ClientInfo("my-url-shortener", "1.0"), 
                threatInfo
            );

            SafeBrowsingResponse response = safeBrowsingClient.checkUrl(googleAPIKey, request);
            
            // If 'matches' is null or empty, the URL is safe.
            return response.matches() != null && !response.matches().isEmpty();
    }

    public String createShortUrl(String originalUrl, String username, Integer daysValid) {
    	
    	if (isUnsafe(originalUrl)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "URL is flagged as malicious!");
        }
    	
        // 1. Get next unique long ID from our in-memory buffer
        long uniqueId = userBuffer.getNextId(username);

        // 2. Convert that number to a short string
        String shortCode = Base62.encode(uniqueId);

        // 3. Save to MongoDB
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode(shortCode);
        mapping.setOriginalUrl(originalUrl);
        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setCreatedBy(username);
     // If daysValid is null, default to 30 days, or keep it null for permanent
        if (daysValid != null) {
            mapping.setExpiresAt(LocalDateTime.now().plusDays(daysValid));
        }
        urlRepository.save(mapping);
        
        long ttlSeconds = (daysValid != null) ? daysValid * 86400L : 86400L; // Default 24h if permanent
        redisTemplate.opsForValue().set(REDIS_PREFIX + shortCode, originalUrl, Duration.ofHours(ttlSeconds));

        return shortCode;
    }

    public String resolveUrl(String shortCode) {
    	// 1. Ignore favicon requests to prevent DB noise
        if (shortCode.equals("favicon.ico")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        
    	
        // 2. Try to fetch from Redis first
        String cacheKey = REDIS_PREFIX + shortCode;
        String cachedUrl = redisTemplate.opsForValue().get(cacheKey);
        if (cachedUrl != null) {
            System.out.println("Cache Hit for: " + shortCode);
            return cachedUrl;
        }
        
     // 3. Cache Miss - Go to MongoDB
        System.out.println("Cache Miss for: " + shortCode);
        return urlRepository.findByShortCode(shortCode)
        		.map(mapping -> {
        			if (mapping.getExpiresAt() != null && mapping.getExpiresAt().isBefore(LocalDateTime.now())) {
                        throw new ResponseStatusException(HttpStatus.GONE, "Link has expired");
                    }
                    redisTemplate.opsForValue().set(cacheKey, mapping.getOriginalUrl(), Duration.ofHours(24));
                    return mapping.getOriginalUrl();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found"));
       
    }
}
