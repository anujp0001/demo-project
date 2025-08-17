package com.prj.ticketmanagementsystem.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String token;
    private String role;
}
