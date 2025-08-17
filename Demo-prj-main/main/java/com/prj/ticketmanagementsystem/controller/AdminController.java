package com.prj.ticketmanagementsystem.controller;

import com.prj.ticketmanagementsystem.dto.RegisterRequest;
import com.prj.ticketmanagementsystem.dto.request.AssignTicketRequest;
import com.prj.ticketmanagementsystem.dto.response.TicketResponse;
import com.prj.ticketmanagementsystem.entity.Ticket;
import com.prj.ticketmanagementsystem.entity.User;
import com.prj.ticketmanagementsystem.enums.Role;
import com.prj.ticketmanagementsystem.enums.TicketStatus;
import com.prj.ticketmanagementsystem.service.TicketService;
import com.prj.ticketmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TicketService ticketService;
    
    // Create support agent
    @PostMapping("/support-agents")
    public ResponseEntity<String> createSupportAgent(@RequestBody RegisterRequest request) {
        User supportAgent = userService.createUser(
            request.getUsername(),
            request.getPassword(),
            request.getEmail(),
            Role.SUPPORT_AGENT
        );
        return ResponseEntity.ok("Support agent created successfully");
    }
    
    // Create admin
    @PostMapping("/admins")
    public ResponseEntity<String> createAdmin(@RequestBody RegisterRequest request) {
        User admin = userService.createUser(
            request.getUsername(),
            request.getPassword(),
            request.getEmail(),
            Role.ADMIN
        );
        return ResponseEntity.ok("Admin created successfully");
    }
    
    // Get all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
    
    // Delete user
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
    
    // Update user role
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long userId, @RequestParam Role role) {
        userService.updateUserRole(userId, role);
        return ResponseEntity.ok("User role updated successfully");
    }
    
    // Get all tickets
    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponse>> getAllTickets(Authentication authentication) {
        User admin = getCurrentUser(authentication);
        List<Ticket> tickets = ticketService.getAllTickets(admin);
        List<TicketResponse> ticketResponses = tickets.stream()
                .map(this::convertToTicketResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ticketResponses);
    }
    
    // Force reassign ticket
    @PutMapping("/tickets/{ticketId}/reassign")
    public ResponseEntity<TicketResponse> reassignTicket(@PathVariable Long ticketId,
                                                        @RequestBody AssignTicketRequest request,
                                                        Authentication authentication) {
        User admin = getCurrentUser(authentication);
        Ticket reassignedTicket = ticketService.assignTicket(ticketId, request.getSupportAgentId(), admin);
        return ResponseEntity.ok(convertToTicketResponse(reassignedTicket));
    }
    
    // Force close ticket
    @PutMapping("/tickets/{ticketId}/force-close")
    public ResponseEntity<TicketResponse> forceCloseTicket(@PathVariable Long ticketId,
                                                          Authentication authentication) {
        User admin = getCurrentUser(authentication);
        Ticket closedTicket = ticketService.changeTicketStatus(ticketId, TicketStatus.CLOSED, admin);
        return ResponseEntity.ok(convertToTicketResponse(closedTicket));
    }
    
    // Get support agents
    @GetMapping("/support-agents")
    public ResponseEntity<List<User>> getSupportAgents() {
        List<User> supportAgents = userService.findByRole(Role.SUPPORT_AGENT);
        return ResponseEntity.ok(supportAgents);
    }
    
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username);
    }
    
    private TicketResponse convertToTicketResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setSubject(ticket.getSubject());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setCreatedByUsername(ticket.getCreatedBy().getUsername());
        response.setAssignedToUsername(ticket.getAssignedTo() != null ? 
                                      ticket.getAssignedTo().getUsername() : null);
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        return response;
    }
}
