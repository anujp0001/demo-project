package com.prj.ticketmanagementsystem.service;

import com.prj.ticketmanagementsystem.entity.Comment;
import com.prj.ticketmanagementsystem.entity.Ticket;
import com.prj.ticketmanagementsystem.entity.User;
import com.prj.ticketmanagementsystem.repository.CommentRepository;
import com.prj.ticketmanagementsystem.exception.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private TicketService ticketService;
    
    // Add comment to ticket
    public Comment addComment(Long ticketId, String content, User author) {
        Ticket ticket = ticketService.getTicketWithAccessControl(ticketId, author);
        Comment comment = new Comment(content, ticket, author);
        return commentRepository.save(comment);
    }
    
    // Get comments for a ticket
    public List<Comment> getCommentsForTicket(Long ticketId, User user) {
        Ticket ticket = ticketService.getTicketWithAccessControl(ticketId, user);
        return commentRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }
    
    // Get user's comments
    public List<Comment> getUserComments(User user) {
        return commentRepository.findByAuthorOrderByCreatedAtDesc(user);
    }
}
