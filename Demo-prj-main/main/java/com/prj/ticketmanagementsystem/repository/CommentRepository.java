package com.prj.ticketmanagementsystem.repository;

import com.prj.ticketmanagementsystem.entity.Comment;
import com.prj.ticketmanagementsystem.entity.Ticket;
import com.prj.ticketmanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Get comments for a ticket ordered by creation time
    List<Comment> findByTicketOrderByCreatedAtAsc(Ticket ticket);
    
    // Get comments by author
    List<Comment> findByAuthorOrderByCreatedAtDesc(User author);
    
    // Count comments for a ticket
    Long countByTicket(Ticket ticket);
}
