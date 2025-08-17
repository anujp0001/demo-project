package com.prj.ticketmanagementsystem.dto.request;

import com.prj.ticketmanagementsystem.enums.Priority;
import com.prj.ticketmanagementsystem.enums.TicketStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketRequest {
    private String subject;
    private String description;
    private Priority priority;
    private TicketStatus status;
}
