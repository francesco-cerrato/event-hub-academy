package com.academy.eventhub.controller;

import com.academy.eventhub.dto.EventRequestDto;
import com.academy.eventhub.dto.EventResponseDto;
import com.academy.eventhub.dto.TicketRequestDto;
import com.academy.eventhub.dto.TicketResponseDto;
import com.academy.eventhub.entity.Ticket;
import com.academy.eventhub.service.EventService;
import com.academy.eventhub.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:8081")
@Tag(name = "Events", description = "Endpoint per la consultazione pubblica, la pianificazione " +
        "e la prenotazione degli eventi.")

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

    @Operation(
            summary = "Elenca gli eventi con filtri e paginazione",
            description = "PUNTI 1 e 2 dello Step 10: Consente di cercare gli eventi combinando specifiche dinamiche (data, sede, organizzatore, tag). " +
                    "Include il supporto nativo a paginazione e sorting tramite i parametri standard '?page=0&size=5&sort=eventDate,desc'. " +
                    "Questo endpoint è accessibile senza autenticazione."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagina degli eventi restituita con successo")
    })
    @GetMapping
    public ResponseEntity<Page<EventResponseDto>> getAllEvents(
            /*
                Si utilizza l'annotazione @RequestParam(required = false) in modo che
                tutti i filtri siano opzionali.
                Se l'utente non li passa nella richiesta,
                Spring assegnerà null e mostrerà l'intera lista senza filtri.
             */
            @Parameter(description = "Filtra per data dell'evento (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Filtra per ID identificativo della sede") @RequestParam(required = false) Long venueId,
            @Parameter(description = "Filtra per username esatto dell'organizzatore") @RequestParam(required = false) String organizer,
            @Parameter(description = "Filtra per nome singolo del tag associato") @RequestParam(required = false) String tag,
            @Parameter(description = "Parametri di paginazione e ordinamento (es. page, size, sort)") Pageable pageable)
    {
        // Passiamo tutti i parametri opzionali e il pageable al service per la query dinamica
        Page<EventResponseDto> eventPage  = eventService.getAllEvents(date, venueId, organizer, tag, pageable);
        return ResponseEntity.ok(eventPage);
    }

    @Operation(
            summary = "Recupera un evento tramite ID",
            description = "Restituisce i dettagli di un singolo evento. Questo endpoint è PUBBLICO e accessibile senza autenticazione."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dettagli dell'evento trovati e restituiti"),
            @ApiResponse(responseCode = "404", description = "Evento non trovato con l'ID fornito")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id)
    {
        EventResponseDto foundEvent = eventService.getEventById(id);
        return ResponseEntity.ok(foundEvent);
    }

    @Operation(
            summary = "Pianifica un nuovo evento",
            description = "Registra un nuovo evento associandolo automaticamente all'utente richiedente come organizzatore. " +
                    "Accesso limitato agli utenti con ruolo ORGANIZER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento creato con successo"),
            @ApiResponse(responseCode = "400", description = "Payload non valido o errori di validazione sui vincoli dei prezzi"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesto il ruolo ORGANIZER")
    })
    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto eventRequestDto, Principal principal)
    {
       EventResponseDto createdEvent = eventService.createEvent(eventRequestDto, principal.getName());
       return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Modifica un evento esistente",
            description = "Aggiorna le informazioni di un evento. Il sistema verifica sia l'autenticazione " +
                    "sia che il richiedente sia l'effettivo creatore (organizzatore) dell'evento. " +
                    "Accesso limitato agli utenti con ruolo ORGANIZER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento modificato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati del payload non validi"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: non sei l'organizzatore proprietario o non possiedi il ruolo richiesto"),
            @ApiResponse(responseCode = "404", description = "Evento non trovato")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id,
                                                        @Valid @RequestBody EventRequestDto eventRequestDto,
                                                        Principal principal)
    {
        EventResponseDto updatedEvent = eventService.updateEvent(id, eventRequestDto, principal.getName());
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(
            summary = "Annulla un evento dal sistema",
            description = "Elimina permanentemente un evento. Richiede che l'utente loggato sia il proprietario dell'evento. " +
                    "Accesso limitato agli utenti con ruolo ORGANIZER."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento rimosso con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: non sei il proprietario dell'evento o non possiedi il ruolo richiesto"),
            @ApiResponse(responseCode = "404", description = "Evento non trovato")
    })
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


    @Operation(
            summary = "Prenota un biglietto per l'evento",
            description = "REGOLA DELLO STEP 7: Consente di staccare un biglietto (STANDARD o VIP) per l'evento selezionato. " +
                    "Esegue i controlli sulla capienza residua complessiva della sede. " +
                    "Accesso consentito a USER, ORGANIZER e ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Biglietto prenotato con successo"),
            @ApiResponse(responseCode = "400", description = "Posti esauriti nella sede o tipologia di biglietto errata"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "404", description = "Evento non trovato")
    })
    @PostMapping("/{id}/book") // si aggancia a /api/events diventando /api/events/{id}/book
    public ResponseEntity<TicketResponseDto> createTicket(@PathVariable Long id, @Valid @RequestBody TicketRequestDto dto,
                                               Principal principal)
    {
        TicketResponseDto createdTicket = ticketService.createTicket(id, dto, principal.getName());
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

}
