package com.prj.ticketmanagementsystem.service;

import com.prj.ticketmanagementsystem.entity.User;
import com.prj.ticketmanagementsystem.enums.Role;
import com.prj.ticketmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
    
    public Optional<User> findByUsernameOptional(String username) {
        return userRepository.findByUsername(username);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    public User createUser(String username, String password, String email, Role role) {
        if (existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        User user = new User(username, password, email, role);
        return save(user);
    }
    
    public void deleteUser(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }
    
    public User updateUserRole(Long userId, Role newRole) {
        User user = findById(userId);
        user.setRole(newRole);
        return save(user);
    }
}
