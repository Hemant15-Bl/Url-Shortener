package com.tp.main.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tp.main.security.DownstreamSecurityFilter;
import com.tp.main.security.JwtUtils;



@Configuration
@EnableWebSecurity
@Import({DownstreamSecurityFilter.class, JwtUtils.class})
public class AnalyticSecurityConfig {

	private final DownstreamSecurityFilter downstreamSecurityFilter;
	
	public AnalyticSecurityConfig(@Lazy DownstreamSecurityFilter downstreamSecurityFilter) {
        this.downstreamSecurityFilter = downstreamSecurityFilter;
    }
	
	@Bean
	@Primary
	public FilterRegistrationBean<DownstreamSecurityFilter> registration(DownstreamSecurityFilter filter) {
	    FilterRegistrationBean<DownstreamSecurityFilter> registration = new FilterRegistrationBean<>(filter);
	    registration.setEnabled(false); // Stops it from running outside of Spring Security
	    return registration;
	}
	
	@Bean
	@Primary
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth.requestMatchers("/eureka/**", "/actuator/**", "/api/v3/analytics/**").permitAll()
//												.requestMatchers("/api/v3/analytics/**").authenticated()
											.anyRequest().authenticated()
								)
			// 1. Handle OAuth2.O (Google)
//			.oauth2Login(oauth2 -> oauth2.redirectionEndpoint(redirect -> redirect.baseUri("/login/oauth2/code/*")))
			// 2. Handle Custom JWT/Internal Secret
			.addFilterBefore(downstreamSecurityFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}
