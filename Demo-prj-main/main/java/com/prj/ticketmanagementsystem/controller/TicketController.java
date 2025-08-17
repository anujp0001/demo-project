package com.prj.ticketmanagementsystem.controller;

import com.prj.ticketmanagementsystem.dto.request.CreateTicketRequest;
import com.prj.ticketmanagementsystem.dto.request.UpdateTicketRequest;
import com.prj.ticketmanagementsystem.dto.request.AssignTicketRequest;
import com.prj.ticketmanagementsystem.dto.response.TicketResponse;
import com.prj.ticketmanagementsystem.dto.response.CommentResponse;
import com.prj.ticketmanagementsystem.entity.Ticket;
import com.prj.ticketmanagementsystem.entity.Comment;
import com.prj.ticketmanagementsystem.entity.User;
import com.prj.ticketmanagementsystem.enums.TicketStatus;
import com.prj.ticketmanagementsystem.service.TicketService;
import com.prj.ticketmanagementsystem.service.CommentService;
import com.prj.ticketmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private UserService userService;
    
    // Create new ticket (All authenticated users)
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody CreateTicketRequest request, 
                                                     Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Ticket ticket = ticketService.createTicket(
            request.getSubject(), 
            request.getDescription(), 
            request.getPriority(), 
            currentUser
        );
        return ResponseEntity.ok(convertToTicketResponse(ticket));
    }
    
    // Get user's tickets
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getUserTickets(Authentication authentication,
                                                             @RequestParam(required = false) TicketStatus status) {
        User currentUser = getCurrentUser(authentication);
        List<Ticket> tickets;
        
        if (status != null) {
            tickets = ticketService.getTicketsByStatus(status, currentUser);
        } else {
            tickets = ticketService.getTicketsForUser(currentUser);
        }
        
        List<TicketResponse> ticketResponses = tickets.stream()
                .map(this::convertToTicketResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ticketResponses);
    }
    
    // Get specific ticket
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long ticketId, 
                                                   Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Ticket ticket = ticketService.getTicketWithAccessControl(ticketId, currentUser);
        return ResponseEntity.ok(convertToTicketResponse(ticket));
    }
    
    // Update ticket
    @PutMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable Long ticketId,
                                                      @RequestBody UpdateTicketRequest request,
                                                      Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Ticket updatedTicket = ticketService.updateTicket(
            ticketId, 
            request.getSubject(), 
            request.getDescription(), 
            request.getPriority(), 
            request.getStatus(), 
            currentUser
        );
        return ResponseEntity.ok(convertToTicketResponse(updatedTicket));
    }
    
    // Change ticket status (Support agents and admins)
    @PutMapping("/{ticketId}/status")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> changeTicketStatus(@PathVariable Long ticketId,
                                                           @RequestParam TicketStatus status,
                                                           Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Ticket updatedTicket = ticketService.changeTicketStatus(ticketId, status, currentUser);
        return ResponseEntity.ok(convertToTicketResponse(updatedTicket));
    }
    
    // Assign ticket to support agent (Admin only)
    @PutMapping("/{ticketId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> assignTicket(@PathVariable Long ticketId,
                                                      @RequestBody AssignTicketRequest request,
                                                      Authentication authentication) {
        User admin = getCurrentUser(authentication);
        Ticket assignedTicket = ticketService.assignTicket(ticketId, request.getSupportAgentId(), admin);
        return ResponseEntity.ok(convertToTicketResponse(assignedTicket));
    }
    
    // Helper method to get current user
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username);
    }
    
    // Helper method to convert Ticket to TicketResponse
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
        
        // Include comments if available
        if (ticket.getComments() != null) {
            List<CommentResponse> commentResponses = ticket.getComments().stream()
                    .map(this::convertToCommentResponse)
                    .collect(Collectors.toList());
            response.setComments(commentResponses);
        }
        
        return response;
    }
    
    // Helper method to convert Comment to CommentResponse
    private CommentResponse convertToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setAuthorUsername(comment.getAuthor().getUsername());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}
