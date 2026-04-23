package com.tp.main.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtils {

//	private final SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private final String secret = "afafasfafafasfafafasfafacasdasfasxASFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

	private static final long ACCESS_TOKEN_EXPIRATION = 900000; // 15 Minutes (in milliseconds)
	private static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 15 Minutes (in milliseconds)
	
//	    public String generateToken(String username) {
//	        return Jwts.builder()
//	                .setSubject(username)
//	                .setIssuedAt(new Date())
//	                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//	                .signWith(SignatureAlgorithm.HS256, secret)
//	                .compact();
//	    }
	    
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
	    public boolean validateToken(String token, String username) {
	        final String extractedUsername = extractUsername(token);
	        return (extractedUsername.equals(username) && !isTokenExpired(token));
	    }
	    
	    public boolean validateRefreshToken(String token) {
		       try {
		    	   Claims claims = extractAllClaims(token);
			        return "refresh".equals(claims.get("type")) && !isTokenExpired(token);
		       }catch(JwtException e) {
		    	   System.out.println(" Refresh Token Invalid!!");
		    	   return false;
		       }
		    }
	    
	    public String generateToken(String username) {
	        Map<String, Object> claims = new HashMap<>();
	        return createToken(claims, username, ACCESS_TOKEN_EXPIRATION);
	    }
	    
	    public String generateRefreshToken(String username) {
	        Map<String, Object> claims = new HashMap<>();
	        claims.put("type", "refresh"); // Distinguish from access token
	        return createToken(claims, username, REFRESH_TOKEN_EXPIRATION);
	    }
	    
	    

	    private String createToken(Map<String, Object> claims, String subject, long expiration) {
	        return Jwts.builder()
	                .setClaims(claims)
	                .setSubject(subject)
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + expiration))
	                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Ensure getSigningKey() uses your secret
	                .compact();
	    }
	    
	    private SecretKey getSigningKey() {
	        byte[] keyBytes = Decoders.BASE64.decode(secret);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }
}
