package com.tp.main.security;

import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtils {

	private final String secret = "afafasfafafasfafafasfafacasdasfasxASFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";
	private long expiration = 86400000; // 24 hours in milliseconds

	    
	 // 1. Extract the username (subject) from the token
	    public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);
	    }

	    // 2. Generic method to extract any data (claim) from the token
	    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = extractAllClaims(token);
	        return claimsResolver.apply(claims);
	    }

	    private Claims extractAllClaims(String token) {
	        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	    }

	    // 3. Check if the token is expired
	    public boolean isTokenExpired(String token) {
	        return extractClaim(token, Claims::getExpiration).before(new Date());
	    }

	    // 4. Validate the token (checks username and expiration)
	    public boolean validateToken(String token) {
	       try {
	    	   extractAllClaims(token);
		        return !isTokenExpired(token);
	       }catch(JwtException e) {
	    	   System.out.println("Token Invalid!!");
	    	   return false;
	       }
	    }
}
