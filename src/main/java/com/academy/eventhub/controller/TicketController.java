package com.academy.eventhub.controller;

import com.academy.eventhub.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Endpoint utente per l'annullamento dei biglietti acquistati")
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
    @Operation(
            summary = "Cancella una prenotazione",
            description = "Consente a un utente di annullare il proprio biglietto. Il sistema effettua un controllo di titolarità prima di procedere."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Biglietto rimosso con successo"),
            @ApiResponse(responseCode = "403", description = "Operazione negata: non sei il proprietario del biglietto"),
            @ApiResponse(responseCode = "404", description = "Biglietto non trovato")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id, Principal principal)
    {
        ticketService.deleteTicket(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
