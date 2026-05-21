package com.academy.eventhub.controller;

import com.academy.eventhub.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/tickets")
public class TicketController
{

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService)
    {
        this.ticketService = ticketService;
    }


    /*
        Questo endpoint risponde a DELETE /api/tickets/{id}
        Estrae l'utente autenticato tramite Principal per passarlo al service,
        garantendo che un utente non possa cancellare biglietti di altre persone.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id, Principal principal)
    {
        ticketService.deleteTicket(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
