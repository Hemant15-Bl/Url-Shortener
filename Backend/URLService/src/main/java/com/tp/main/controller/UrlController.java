package com.tp.main.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tp.main.dto.ClickEvent;
import com.tp.main.entities.UrlMapping;
import com.tp.main.externalservices.AnalyticClient;
import com.tp.main.repository.UrlRepository;
import com.tp.main.services.UrlServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UrlController {

	@Autowired
    private UrlServiceImpl urlService;
	
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	@Autowired
	private AnalyticClient analyticClient;

	@Autowired
	private UrlRepository urlRepository;
	
    // 1. Endpoint to Create Short URL
    @PostMapping("/api/v2/url/shorten")
    public ResponseEntity<String> shorten(@RequestBody String originalUrl, HttpServletRequest request, @RequestHeader("X-User-Header") String username, @RequestParam(required = false) Integer daysValid) {
        String shortCode = urlService.createShortUrl(originalUrl, username,daysValid);
        String domain = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return ResponseEntity.ok(domain+"/" + shortCode);
    }

    // 2. Endpoint to Redirect (The "Magic")
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
    	String userAgent = request.getHeader("User-Agent");
    	String ipAddress = request.getRemoteAddr();
    	
    	ClickEvent event = new ClickEvent(shortCode, ipAddress, userAgent, LocalDateTime.now());
    	
        String originalUrl = urlService.resolveUrl(shortCode);
        
     // Safety check: Ensure the URL is redirect-ready
        if (originalUrl != null && !originalUrl.startsWith("http")) {
            originalUrl = "https://" + originalUrl;
        }
        
     // Fire and forget: send to Kafka
        kafkaTemplate.send("url-clicks", event);
        
        return ResponseEntity.status(HttpStatus.FOUND) // HTTP 302
                .location(URI.create(originalUrl))
                .build();
    }
    
    @GetMapping("/my-links")
    public ResponseEntity<List<UrlMapping>> getMyLinks(@RequestHeader("X-User-Header") String username) {
        return ResponseEntity.ok(urlRepository.findByCreatedBy(username));
    }
    
    @GetMapping("/api/v2/url/all")
    public ResponseEntity<List<UrlMapping>> getAllLinks() {
        return ResponseEntity.ok(urlRepository.findAll());
    }
    
    @DeleteMapping("/api/v2/url/remove/{shortCode}")
    public ResponseEntity<Void> deleteFullUrl(@PathVariable String shortCode) {
        // 1. Delete from URL-Service's own Database
        urlRepository.deleteByShortCode(shortCode);

        // 2. Call Analytic-Service to clean up stats and logs
        analyticClient.removeAnalytics(shortCode);

        return ResponseEntity.noContent().build();
    }
}
