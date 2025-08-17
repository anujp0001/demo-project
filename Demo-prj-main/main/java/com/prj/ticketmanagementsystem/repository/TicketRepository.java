package com.prj.ticketmanagementsystem.repository;

import com.prj.ticketmanagementsystem.entity.Ticket;
import com.prj.ticketmanagementsystem.entity.User;
import com.prj.ticketmanagementsystem.enums.TicketStatus;
import com.prj.ticketmanagementsystem.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Users see only their own tickets
    List<Ticket> findByCreatedByOrderByCreatedAtDesc(User user);
    
    // Support agents see assigned tickets
    List<Ticket> findByAssignedToOrderByCreatedAtDesc(User user);
    
    // Filter by status
    List<Ticket> findByStatusOrderByCreatedAtDesc(TicketStatus status);
    
    // Filter by priority
    List<Ticket> findByPriorityOrderByCreatedAtDesc(Priority priority);
    
    // User's tickets by status
    List<Ticket> findByCreatedByAndStatusOrderByCreatedAtDesc(User user, TicketStatus status);
    
    // Support agent's assigned tickets by status
    List<Ticket> findByAssignedToAndStatusOrderByCreatedAtDesc(User user, TicketStatus status);
    
    // Find by ID and validate ownership
    @Query("SELECT t FROM Ticket t WHERE t.id = :ticketId AND t.createdBy.username = :username")
    Optional<Ticket> findByIdAndCreatedBy(@Param("ticketId") Long ticketId, @Param("username") String username);
    
    // Find unassigned tickets (for support agents to pick up)
    List<Ticket> findByAssignedToIsNullAndStatusOrderByCreatedAtDesc(TicketStatus status);
}
