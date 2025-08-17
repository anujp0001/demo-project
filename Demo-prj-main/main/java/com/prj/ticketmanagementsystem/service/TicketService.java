package com.prj.ticketmanagementsystem.service;

import com.prj.ticketmanagementsystem.entity.Ticket;
import com.prj.ticketmanagementsystem.entity.User;
import com.prj.ticketmanagementsystem.enums.Role;
import com.prj.ticketmanagementsystem.enums.TicketStatus;
import com.prj.ticketmanagementsystem.enums.Priority;
import com.prj.ticketmanagementsystem.repository.TicketRepository;
import com.prj.ticketmanagementsystem.exception.AccessDeniedException;
import com.prj.ticketmanagementsystem.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private UserService userService;
    
    // Create new ticket (USER role)
    public Ticket createTicket(String subject, String description, Priority priority, User creator) {
        Ticket ticket = new Ticket(subject, description, priority, creator);
        return ticketRepository.save(ticket);
    }
    
    // Get tickets based on user role
    public List<Ticket> getTicketsForUser(User user) {
        switch (user.getRole()) {
            case ADMIN:
                return ticketRepository.findAll();
            case SUPPORT_AGENT:
                return ticketRepository.findByAssignedToOrderByCreatedAtDesc(user);
            default:
                return ticketRepository.findByCreatedByOrderByCreatedAtDesc(user);
        }
    }
    
    // Get single ticket with access control
    public Ticket getTicketWithAccessControl(Long ticketId, User user) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        
        if (!canUserAccessTicket(user, ticket)) {
            throw new AccessDeniedException("You don't have permission to access this ticket");
        }
        
        return ticket;
    }
    
    // Update ticket with access control
    public Ticket updateTicket(Long ticketId, String subject, String description, 
                             Priority priority, TicketStatus status, User user) {
        Ticket ticket = getTicketWithAccessControl(ticketId, user);
        
        // Only allow certain updates based on role
        if (user.getRole() == Role.USER && !ticket.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("Users can only update their own tickets");
        }
        
        if (subject != null) ticket.setSubject(subject);
        if (description != null) ticket.setDescription(description);
        if (priority != null) ticket.setPriority(priority);
        
        // Only support agents and admins can change status
        if (status != null && (user.getRole() == Role.SUPPORT_AGENT || user.getRole() == Role.ADMIN)) {
            ticket.setStatus(status);
        }
        
        return ticketRepository.save(ticket);
    }
    
    // Assign ticket to support agent (Admin only)
    public Ticket assignTicket(Long ticketId, Long supportAgentId, User admin) {
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admins can assign tickets");
        }
        
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        
        User supportAgent = userService.findById(supportAgentId);
        if (supportAgent.getRole() != Role.SUPPORT_AGENT) {
            throw new IllegalArgumentException("Can only assign tickets to support agents");
        }
        
        ticket.setAssignedTo(supportAgent);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        return ticketRepository.save(ticket);
    }
    
    // Change ticket status (Support agents and admins)
    public Ticket changeTicketStatus(Long ticketId, TicketStatus newStatus, User user) {
        Ticket ticket = getTicketWithAccessControl(ticketId, user);
        
        if (user.getRole() == Role.USER) {
            throw new AccessDeniedException("Users cannot change ticket status directly");
        }
        
        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }
    
    // Check if user can access ticket
    public boolean canUserAccessTicket(User user, Ticket ticket) {
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        if (user.getRole() == Role.SUPPORT_AGENT && ticket.getAssignedTo() != null 
            && ticket.getAssignedTo().getId().equals(user.getId())) {
            return true;
        }
        return ticket.getCreatedBy().getId().equals(user.getId());
    }
    
    // Get all tickets (Admin only)
    public List<Ticket> getAllTickets(User admin) {
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admins can view all tickets");
        }
        return ticketRepository.findAll();
    }
    
    // Get tickets by status
    public List<Ticket> getTicketsByStatus(TicketStatus status, User user) {
        switch (user.getRole()) {
            case ADMIN:
                return ticketRepository.findByStatusOrderByCreatedAtDesc(status);
            case SUPPORT_AGENT:
                return ticketRepository.findByAssignedToAndStatusOrderByCreatedAtDesc(user, status);
            default:
                return ticketRepository.findByCreatedByAndStatusOrderByCreatedAtDesc(user, status);
        }
    }
}
