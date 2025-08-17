package com.prj.ticketmanagementsystem.dto.request;

import com.prj.ticketmanagementsystem.enums.Priority;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {
    private String subject;
    private String description;
    private Priority priority;
}
