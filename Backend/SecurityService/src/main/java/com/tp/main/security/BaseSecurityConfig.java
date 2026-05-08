package com.tp.main.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class BaseSecurityConfig {

	// Downstream services can override this bean if they have unique paths
	private final DownstreamSecurityFilter downstreamSecurityFilter;

    public BaseSecurityConfig(DownstreamSecurityFilter downstreamSecurityFilter) {
        this.downstreamSecurityFilter = downstreamSecurityFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DefaultSecurityFilterChain filterChain) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
        		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/eureka/**")).permitAll()
                    .anyRequest().authenticated()
                ).addFilterBefore(downstreamSecurityFilter, UsernamePasswordAuthenticationFilter.class)
                   .build();
    }
}
