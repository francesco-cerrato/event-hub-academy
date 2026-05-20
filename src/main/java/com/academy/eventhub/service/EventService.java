package com.academy.eventhub.service;

import com.academy.eventhub.dto.EventRequestDto;
import com.academy.eventhub.dto.EventResponseDto;

import java.util.List;

public interface EventService
{
    // Accetta l'utente loggato per impostarlo come organizer
    EventResponseDto createEvent(EventRequestDto dto, String currentUsername);

    EventResponseDto getEventById(Long id);

    // Precendemente, prima di aggiungere i filtri opzionali (specification) c'era questo metodo:
    //List<EventResponseDto> getAllEvents();
    // Il nuovo metodo, utile per le specification, è il seguente:
    List<EventResponseDto> getAllEvents(java.time.LocalDate date, Long venueId, String organizer, String tag);



    // Accetta l'utente loggato per verificare che sia il vero proprietario dell'evento
    EventResponseDto updateEvent(Long id, EventRequestDto dto, String currentUsername);

    // Accetta l'utente loggato per motivi di sicurezza sulla cancellazione
    void deleteEvent(Long id, String currentUsername);
}
