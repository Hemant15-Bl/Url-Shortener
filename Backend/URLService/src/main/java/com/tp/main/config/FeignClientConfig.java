package com.tp.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignClientConfig {

	@Value("${internal.secret}") // Ensure this is in your URL-SERVICE application.yml
    private String internalSecret;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            // Get the current thread's HTTP request (the one the user sent to URL-SERVICE)
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // 1. Inject the Secret
                template.header("X-Internal-Secret", internalSecret);
                
                // 2. Propagate the User header (the one we got from the Gateway)
                String username = request.getHeader("X-User-Header");
                if (username != null) {
                    template.header("X-User-Header", username);
                }
            }
        };
    }
}
