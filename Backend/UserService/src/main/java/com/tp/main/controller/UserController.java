package com.tp.main.controller;

import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tp.main.dto.AuthRequest;
import com.tp.main.entities.User;
import com.tp.main.repository.UserRepository;
import com.tp.main.security.JwtUtils;
import com.tp.main.services.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {


	@Autowired
    private UserService userService;

	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository
        		.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken!");
        }
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse exchange) {
        // 1. Fetch user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Compare passwords
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        // 3. Generate Token
        String token = jwtUtils.generateToken(user.getUsername());
        String refreshToken = this.jwtUtils.generateRefreshToken(user.getUsername());
        
        Cookie authCookie = new Cookie("AUTH_TOKEN", token);
        		authCookie.setHttpOnly(true);
        		authCookie.setSecure(false); // Set to true in production (HTTPS)
        		authCookie.setPath("/");
        		authCookie.setMaxAge(900);    // 15 minutes

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // Set to true in production (HTTPS)
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(604800); // 7 days

        // 4. Add cookies to the response
        exchange.addCookie(authCookie);
        exchange.addCookie(refreshCookie);
        // 5. Return Token
        return ResponseEntity.ok(Map.of("token", token));
    }
}
