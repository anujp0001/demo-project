package com.prj.ticketmanagementsystem.config;

import com.prj.ticketmanagementsystem.entity.User;
import com.prj.ticketmanagementsystem.enums.Role;
import com.prj.ticketmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!userService.existsByUsername("admin")) {
            User admin = new User(
                "admin",
                passwordEncoder.encode("admin123"),
                "admin@ticketmanagement.com",
                Role.ADMIN
            );
            userService.save(admin);
            System.out.println("Default admin user created: admin/admin123");
        }
        
        // Create default support agent if not exists
        if (!userService.existsByUsername("support")) {
            User support = new User(
                "support",
                passwordEncoder.encode("support123"),
                "support@ticketmanagement.com",
                Role.SUPPORT_AGENT
            );
            userService.save(support);
            System.out.println("Default support user created: support/support123");
        }
        
        // Create default regular user if not exists
        if (!userService.existsByUsername("user")) {
            User user = new User(
                "user",
                passwordEncoder.encode("user123"),
                "user@ticketmanagement.com",
                Role.USER
            );
            userService.save(user);
            System.out.println("Default regular user created: user/user123");
        }
    }
}
