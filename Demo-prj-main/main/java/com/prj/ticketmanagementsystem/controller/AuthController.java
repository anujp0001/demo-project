package com.prj.ticketmanagementsystem.controller;

import com.prj.ticketmanagementsystem.dto.AuthResponse;
import com.prj.ticketmanagementsystem.dto.LoginRequest;
import com.prj.ticketmanagementsystem.dto.RegisterRequest;
import com.prj.ticketmanagementsystem.entity.User;
import com.prj.ticketmanagementsystem.enums.Role;
import com.prj.ticketmanagementsystem.service.UserService;
import com.prj.ticketmanagementsystem.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user credentials
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse("Invalid credentials", null, null));
        }
        
        // Get user details and generate token
        User user = userService.findByUsername(loginRequest.getUsername());
        String jwt = jwtUtil.generateToken(user);
        
        return ResponseEntity.ok(new AuthResponse(
            "Login successful",
            jwt,
            user.getRole().name()
        ));
    }
    
    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        // Check if username already exists
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse("Username already exists", null, null));
        }
        
        // Check if email already exists
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse("Email already exists", null, null));
        }
        
        // Create new user
        User user = new User(
            registerRequest.getUsername(),
            passwordEncoder.encode(registerRequest.getPassword()),
            registerRequest.getEmail(),
            Role.USER // Default role for registration
        );
        
        userService.save(user);
        
        return ResponseEntity.ok(new AuthResponse("User registered successfully", null, null));
    }
    
    // Get current user info
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            String username = jwtUtil.extractUsername(jwt);
            User user = userService.findByUsername(username);
            
            return ResponseEntity.ok(new AuthResponse(
                "User details retrieved successfully",
                null,
                user.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse("Invalid token", null, null));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // Clear security context
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Logout successful");
    }
}
