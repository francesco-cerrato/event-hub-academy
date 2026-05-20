package com.academy.eventhub.service;

import com.academy.eventhub.dto.EventRequestDto;
import com.academy.eventhub.dto.EventResponseDto;
import com.academy.eventhub.dto.VenueResponseDto;
import com.academy.eventhub.entity.Event;
import com.academy.eventhub.entity.Tag;
import com.academy.eventhub.entity.User;
import com.academy.eventhub.entity.Venue;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.EventRepository;
import com.academy.eventhub.repository.TagRepository;
import com.academy.eventhub.repository.UserRepository;
import com.academy.eventhub.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService{


    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final TagRepository tagRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                            VenueRepository venueRepository, TagRepository tagRepository)
    {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto dto, String currentUsername)
    {
        // Recupera l'organizzatore loggato
        User foundOrganizer = userRepository.findByUsername(currentUsername)
                .orElseThrow( () -> new ResourceNotFoundException("Utente organizer non trovato con username: " + currentUsername));

        // Recupera la sede
        Venue foundVenue = venueRepository.findById(dto.getVenueId())
                .orElseThrow( () -> new ResourceNotFoundException("Sede non trovata con id: " + dto.getVenueId()));

        // Istanzia l'evento
        Event newEvent = new Event(
                dto.getTitle(), dto.getDescription(), dto.getEventDate(),dto.getPrice(),dto.getVipPrice()
        );
        newEvent.setOrganizer(foundOrganizer);
        newEvent.setVenue(foundVenue);

        // Associa i tag se presenti nell'input
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            List<Tag> foundTags = tagRepository.findAllById(dto.getTagIds());
            newEvent.setTags(new HashSet<>(foundTags));
        }

        Event savedEvent = eventRepository.save(newEvent);
        return convertToResponseDto(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEventById(Long id)
    {
        Event foundEvent = eventRepository.findById(id)
                .orElseThrow( () ->  new ResourceNotFoundException("Evento non trovato con id: " + id));

        return convertToResponseDto(foundEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents()
    {
        List<Event> eventList = eventRepository.findAll();
        List<EventResponseDto> dtoList = new ArrayList<>();

        for (Event event : eventList)
        {
            dtoList.add(convertToResponseDto(event));
        }

        return dtoList;
    }

    @Override
    @Transactional
    public EventResponseDto updateEvent(Long id, EventRequestDto dto, String currentUsername)
    {
        Event foundEvent = eventRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Evento non trovato con id: " + id));

        // Verifica se l'organizzatore loggato è il reale proprietario dell'evento
        if (!foundEvent.getOrganizer().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Non sei autorizzato a modificare questo evento, in quanto non lo hai creato tu");
        }

        Venue foundVenue = venueRepository.findById(dto.getVenueId())
                        .orElseThrow( () -> new ResourceNotFoundException("Sede non trovata con id: " + dto.getVenueId()));

        foundEvent.setTitle(dto.getTitle());
        foundEvent.setDescription(dto.getDescription());
        foundEvent.setEventDate(dto.getEventDate());
        foundEvent.setPrice(dto.getPrice());
        foundEvent.setVipPrice(dto.getVipPrice());

        foundEvent.setVenue(foundVenue);

        if (dto.getTagIds() != null) {
            List<Tag> foundTags = tagRepository.findAllById(dto.getTagIds());
            foundEvent.getTags().clear();
            foundEvent.getTags().addAll(foundTags);
        }

        Event updatedEvent = eventRepository.save(foundEvent);
        return convertToResponseDto(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id, String currentUsername)
    {
        Event foundEvent = eventRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Evento non trovato con id: " + id));

        // Verifica se l'organizzatore loggato è il reale proprietario dell'evento
        if (!foundEvent.getOrganizer().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Non sei autorizzato a eliminare questo evento");
        }

        eventRepository.delete(foundEvent);
    }


    // Helper per convertire l'Entity in DTO riutilizzando i vecchi convertitori
    public EventResponseDto convertToResponseDto(Event event)
    {

        VenueResponseDto venueDto = new VenueResponseDto(
                event.getVenue().getId(),
                event.getVenue().getName(),
                event.getVenue().getAddress(),
                event.getVenue().getCapacity()
        );

        Set<String> tagNames = event.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        EventResponseDto eventResponseDto = new EventResponseDto();
        eventResponseDto.setId(event.getId());
        eventResponseDto.setTitle(event.getTitle());
        eventResponseDto.setDescription(event.getDescription());
        eventResponseDto.setEventDate(event.getEventDate());
        eventResponseDto.setPrice(event.getPrice());
        eventResponseDto.setVipPrice(event.getVipPrice());
        eventResponseDto.setOrganizerUsername(event.getOrganizer().getUsername());
        eventResponseDto.setVenue(venueDto);
        eventResponseDto.setTags(tagNames);

        return eventResponseDto;
    }
}
