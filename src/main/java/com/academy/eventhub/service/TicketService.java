package com.academy.eventhub.service;

import com.academy.eventhub.dto.TicketRequestDto;
import com.academy.eventhub.dto.TicketResponseDto;

import java.util.List;

public interface TicketService
{
    TicketResponseDto createTicket(Long eventId,TicketRequestDto ticketRequestDto, String currentUsername);

    void deleteTicket(Long ticketId, String currentUsername);

    List<TicketResponseDto> getUserTickets(String currentUsername);
}
