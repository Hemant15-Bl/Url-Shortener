package com.tp.main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.tp.main.security.JwtAuthenticationFilter;
import com.tp.main.security.OAuth2SuccessHandler;

import reactor.core.publisher.Mono;


@Configuration
@EnableWebFluxSecurity
public class GatewayConfig {

	@Autowired
	private OAuth2SuccessHandler oAuth2SuccessHandler;
	
	@Bean
    public KeyResolver userKeyResolver() {
        // Limits based on the client's IP address
        return exchange -> Mono.just(
            exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        );
    }
	
	@Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow your React app
        config.addAllowedOrigin("http://localhost:3000"); 
        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // Allows GET, POST, DELETE, PUT, OPTIONS
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
	
	 static final String[] endPoints = {"/api/v1/auth/**", "/api/v2/url/**", "/api/v3/analytics/**", "/{shortCode:[a-zA-Z0-9]+}", "/api/v1/kgs/**"};
	
	@Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless API
            .authorizeExchange(auth -> auth
            		.pathMatchers("/login/**").permitAll()
            		.pathMatchers(endPoints).permitAll()
            		.anyExchange().authenticated()
            ).oauth2Login(oauth2 -> oauth2.authenticationSuccessHandler(this.oAuth2SuccessHandler))
            .build();
    }
}
