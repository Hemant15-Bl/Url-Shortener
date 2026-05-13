package com.tp.main.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DownstreamSecurityFilter extends OncePerRequestFilter {

	@Value("${internal.secret}")
	private String internalSecret; // Inject from application.properties

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	    String path = request.getServletPath();
	    // Skip this filter for health checks and discovery
	    return path.startsWith("/actuator") || path.startsWith("/eureka");
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String secret = request.getHeader("X-Internal-Secret");
		if (!internalSecret.equals(secret)) {
			throw new AccessDeniedException("Unauthorized Access");
		}

		// Pass the identity forward
		String username = request.getHeader("X-User-Header");
		// Set SecurityContextHolder so your Controllers can access
		// @AuthenticationPrincipal
		if (username != null)
			setSecurityContext(username);

		filterChain.doFilter(request, response);
	}

	private void setSecurityContext(String username) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
				new ArrayList<>());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	@Bean
	public FilterRegistrationBean<DownstreamSecurityFilter> registration(DownstreamSecurityFilter filter) {
	    FilterRegistrationBean<DownstreamSecurityFilter> registration = new FilterRegistrationBean<>(filter);
	    registration.setEnabled(false); // Stops it from running outside of Spring Security
	    return registration;
	}
}
