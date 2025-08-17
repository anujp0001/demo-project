package com.prj.ticketmanagementsystem.controller;

import com.prj.ticketmanagementsystem.dto.request.CreateCommentRequest;
import com.prj.ticketmanagementsystem.dto.response.CommentResponse;
import com.prj.ticketmanagementsystem.entity.Comment;
import com.prj.ticketmanagementsystem.entity.User;
import com.prj.ticketmanagementsystem.service.CommentService;
import com.prj.ticketmanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private UserService userService;
    
    // Add comment to ticket
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long ticketId,
                                                    @RequestBody CreateCommentRequest request,
                                                    Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Comment comment = commentService.addComment(ticketId, request.getContent(), currentUser);
        return ResponseEntity.ok(convertToCommentResponse(comment));
    }
    
    // Get comments for ticket
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long ticketId,
                                                           Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        List<Comment> comments = commentService.getCommentsForTicket(ticketId, currentUser);
        List<CommentResponse> commentResponses = comments.stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(commentResponses);
    }
    
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username);
    }
    
    private CommentResponse convertToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setAuthorUsername(comment.getAuthor().getUsername());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}
