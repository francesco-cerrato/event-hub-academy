package com.academy.eventhub.controller;

import com.academy.eventhub.dto.EventRequestDto;
import com.academy.eventhub.dto.EventResponseDto;
import com.academy.eventhub.service.EventService;
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

    @Autowired
    public EventController(EventService eventService)
    {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents()
    {
        List<EventResponseDto> eventList = eventService.getAllEvents();
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

}
