package com.academy.eventhub.service;

import com.academy.eventhub.dto.TicketRequestDto;
import com.academy.eventhub.dto.TicketResponseDto;

public interface TicketService
{
    TicketResponseDto createTicket(Long eventId,TicketRequestDto ticketRequestDto, String currentUsername);

    void deleteTicket(Long ticketId, String currentUsername);
}
