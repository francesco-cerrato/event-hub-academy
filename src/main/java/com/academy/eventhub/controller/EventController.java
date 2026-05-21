package com.academy.eventhub.controller;

import com.academy.eventhub.dto.EventRequestDto;
import com.academy.eventhub.dto.EventResponseDto;
import com.academy.eventhub.dto.TicketRequestDto;
import com.academy.eventhub.dto.TicketResponseDto;
import com.academy.eventhub.entity.Ticket;
import com.academy.eventhub.service.EventService;
import com.academy.eventhub.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController
{

    /*
        Il parametro 'Principal' viene iniettato automaticamente da Spring Security
        e rappresenta l'utente attualmente autenticato nel sistema.
        Tramite 'principal.getName()' viene estratto lo username (o l'identificativo unico)
        di chi sta effettuando la richiesta, consentendo al service di verificare
        se l'utente ha effettivamente i permessi per effettuare la specifica richiesta.
     */

    private final EventService eventService;
    private final TicketService ticketService; // Iniettato per gestire la prenotazione dei biglietti

    @Autowired
    public EventController(EventService eventService, TicketService ticketService)
    {
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    /*
    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents()
    {
        List<EventResponseDto> eventList = eventService.getAllEvents();
        return ResponseEntity.ok(eventList);
    }

    Metodo "getAllEvents()" precedete all'inserimento delle specification (filtri)

     */

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents(
            /*
                Si utilizza l'annotazione @RequestParam(required = false) in modo che
                tutti i filtri siano opzionali.
                Se l'utente non li passa nella richiesta,
                Spring assegnerà null e mostrerà l'intera lista senza filtri.
             */
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            @RequestParam(required = false) Long venueId,
            @RequestParam(required = false) String organizer,
            @RequestParam(required = false) String tag)
    {
        // Passiamo tutti i parametri opzionali al service per la query dinamica
        List<EventResponseDto> eventList = eventService.getAllEvents(date, venueId, organizer, tag);
        return ResponseEntity.ok(eventList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id)
    {
        EventResponseDto foundEvent = eventService.getEventById(id);
        return ResponseEntity.ok(foundEvent);
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto eventRequestDto, Principal principal)
    {
       EventResponseDto createdEvent = eventService.createEvent(eventRequestDto, principal.getName());
       return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id,
                                                        @Valid @RequestBody EventRequestDto eventRequestDto,
                                                        Principal principal)
    {
        EventResponseDto updatedEvent = eventService.updateEvent(id, eventRequestDto, principal.getName());
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, Principal principal)
    {
        eventService.deleteEvent(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    /*
       DIFFERENZA TRA I CONTROLLER:
         Questo endpoint POST viene inserito in EventController perché la struttura dell'URL
         richiesta dalla traccia è "/events/{id}/book". Logicamente, l'azione di prenotazione
         nasce come un'operazione contestuale a uno specifico evento.

         L'endpoint di eliminazione della prenotazione ("DELETE /tickets/{id}") viene invece
         implementato all'interno di un TicketController separato. Questo perché la cancellazione
         agisce in modo mirato e isolato sulla singola risorsa del Ticket, svincolata dal percorso
         dell'evento.
    */

    @PostMapping("/{id}/book") // si aggancia a /api/events diventando /api/events/{id}/book
    public ResponseEntity<TicketResponseDto> createTicket(@PathVariable Long id, @Valid @RequestBody TicketRequestDto dto,
                                               Principal principal)
    {
        TicketResponseDto createdTicket = ticketService.createTicket(id, dto, principal.getName());
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

}
