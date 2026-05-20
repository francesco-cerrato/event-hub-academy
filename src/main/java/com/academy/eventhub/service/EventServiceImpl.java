package com.academy.eventhub.service;

import com.academy.eventhub.dto.EventRequestDto;
import com.academy.eventhub.dto.EventResponseDto;
import com.academy.eventhub.dto.VenueResponseDto;
import com.academy.eventhub.entity.*;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.*;
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
    private final SpeakerRepository speakerRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                            VenueRepository venueRepository, TagRepository tagRepository,
                            SpeakerRepository speakerRepository)
    {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
        this.tagRepository = tagRepository;
        this.speakerRepository = speakerRepository;
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

        if (dto.getSpeakerIds() != null && !dto.getSpeakerIds().isEmpty()) {
            List<Speaker> foundSpeakers = speakerRepository.findAllById(dto.getSpeakerIds());
            newEvent.setSpeakers(new HashSet<>(foundSpeakers));
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


    /*
    Precedenemente, prima dell'aggiunta delle specification e dunque della modifica
    del metodo "getAllEvents()", era utile il metodo sottostante.

    La nuova versione del metodo "getAllEvents()" contiene le specification
    per poter filtrare gli eventi in base ad un data specifica, in base alla sede
    specifica, in base allo username dell'organizzatore e in base al nome
    di un Tag

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
    */


    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents(java.time.LocalDate date, Long venueId, String organizer, String tag)
    {
        // Creazione della specifica di base partendo dalla prima (hasDate)
        org.springframework.data.jpa.domain.Specification<Event> spec =
                com.academy.eventhub.specification.EventSpecifications.hasDate(date);

        // Concatenazione  delle altre in cascata usando l'operatore fluente .and()
        spec = spec.and(com.academy.eventhub.specification.EventSpecifications.hasVenueId(venueId))
                .and(com.academy.eventhub.specification.EventSpecifications.hasOrganizerUsername(organizer))
                .and(com.academy.eventhub.specification.EventSpecifications.hasTagName(tag));

        // Esecuzione della query dinamica sul database sfruttando il JpaSpecificationExecutor
        List<Event> eventList = eventRepository.findAll(spec);
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

        if (dto.getSpeakerIds() != null) {
            List<Speaker> foundSpeakers = speakerRepository.findAllById(dto.getSpeakerIds());
            foundEvent.getSpeakers().clear();
            foundEvent.getSpeakers().addAll(foundSpeakers);
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

        Set<String> speakerNames = event.getSpeakers().stream()
                .map(Speaker::getName)
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
        eventResponseDto.setSpeakers(speakerNames);

        return eventResponseDto;
    }
}
