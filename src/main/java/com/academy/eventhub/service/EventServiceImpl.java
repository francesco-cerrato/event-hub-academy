package com.academy.eventhub.service;

import com.academy.eventhub.dto.EventRequestDto;
import com.academy.eventhub.dto.EventResponseDto;
import com.academy.eventhub.dto.VenueResponseDto;
import com.academy.eventhub.entity.*;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
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
    private final TicketRepository ticketRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                            VenueRepository venueRepository, TagRepository tagRepository,
                            SpeakerRepository speakerRepository, TicketRepository ticketRepository)
    {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
        this.tagRepository = tagRepository;
        this.speakerRepository = speakerRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public EventResponseDto createEvent(EventRequestDto dto, String currentUsername)
    {
        // Recupera l'organizzatore loggato
        User foundOrganizer = userRepository.findByUsername(currentUsername)
                .orElseThrow( () -> new ResourceNotFoundException("Utente organizer non trovato con username: " + currentUsername));

        // Blocca subito l'esecuzione se i prezzi non sono validi
        validateEventPrices(dto.getPrice(), dto.getVipPrice());

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
    public Page<EventResponseDto> getAllEvents(java.time.LocalDate date, Long venueId, String organizer, String tag, Pageable pageable)
    {
        // Creazione della specifica di base partendo dalla prima (hasDate)
        org.springframework.data.jpa.domain.Specification<Event> spec =
                com.academy.eventhub.specification.EventSpecifications.hasDate(date);

        // Concatenazione  delle altre in cascata usando l'operatore fluente .and()
        spec = spec.and(com.academy.eventhub.specification.EventSpecifications.hasVenueId(venueId))
                .and(com.academy.eventhub.specification.EventSpecifications.hasOrganizerUsername(organizer))
                .and(com.academy.eventhub.specification.EventSpecifications.hasTagName(tag));

        // Questa porzione di codice (2 righe) era utilizzata prima di aggiornare con Pageable
        // Esecuzione della query dinamica sul database sfruttando il JpaSpecificationExecutor
        //List<Event> eventList = eventRepository.findAll(spec);
        //List<EventResponseDto> dtoList = new ArrayList<>();

        // Esecuzione della query dinamica passando sia la Spec che il Pageable.
        // JpaSpecificationExecutor fornisce nativamente questo overload che restituisce una Page<Event>.
        org.springframework.data.domain.Page<Event> eventPage = eventRepository.findAll(spec, pageable);

        // Trasformazione immediata della Page di Entity in Page di DTO tramite il convertToResponseDto.
        // Il costrutto Lambda si aggancia perfettamente al tuo metodo helper esistente.
        return eventPage.map(event -> this.convertToResponseDto(event));
    }



    @Override
    @Transactional
    public EventResponseDto updateEvent(Long id, EventRequestDto dto, String currentUsername)
    {
        Event foundEvent = eventRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Evento non trovato con id: " + id));


        // Estrazione del ruolo ADMIN dal contesto di sicurezza
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // l'operazione è bloccata se non sei il proprietario e non sei ADMIN
        if (!foundEvent.getOrganizer().getUsername().equals(currentUsername) && !isAdmin) {
            throw new AccessDeniedException("Non sei autorizzato a modificare questo evento.");
        }

        // Blocca subito l'esecuzione se i prezzi non sono validi
        validateEventPrices(dto.getPrice(), dto.getVipPrice());

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

        // NOTA: Con @Transactional il save() esplicito è ridondante, Hibernate aggiorna da solo a fine metodo.
        // Event updatedEvent = eventRepository.save(foundEvent);
        return convertToResponseDto(foundEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id, String currentUsername)
    {
        Event foundEvent = eventRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Evento non trovato con id: " + id));


        // Estrazione del ruolo ADMIN dal contesto di sicurezza
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));


        // l'operazione è bloccata se non sei il proprietario e non sei ADMIN
        if (!foundEvent.getOrganizer().getUsername().equals(currentUsername) && !isAdmin) {
            throw new AccessDeniedException("Non sei autorizzato a eliminare questo evento.");
        }

        eventRepository.delete(foundEvent);
    }

    @Override
    public int getAvailableSeats(Long eventId)
    {
        // Selezione di un evento in base al suo id specifico
        Event foundEvent = eventRepository.findById(eventId)
                .orElseThrow( () -> new ResourceNotFoundException("Evento non trovato con id: " + eventId) );

        // Dato l'evento trovato, si contano, nella tabella tickets, quanti ticket sono "ACTIVE" per l'evento
        long activeTickets = ticketRepository.countByEventIdAndStatus(eventId, TicketStatus.ACTIVE);

        // Recupero dell'intera capacity (capienza) dello specifico evento trovato
        int capacity = foundEvent.getVenue().getCapacity();

        // Calcolo dei posti disponibili: capienza totale - biglietti attivi
        int avaiableSeats = capacity - (int) activeTickets;

        return avaiableSeats;


        /*
        non serve nessun DTO per questo metodo.
        Restituire un tipo primitivo int a livello di Service
        è la scelta più pulita e corretta, perché si tratta
        di un calcolo aritmetico interno alla logica di business.
         */
    }


    // Helper per convertire l'Entity in DTO riutilizzando i vecchi convertitori
    private EventResponseDto convertToResponseDto(Event event)
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


    // Metodo helper per comparare prezzo Standard e prezzo Vip
    private void validateEventPrices(BigDecimal price, BigDecimal vipPrice) {
        if (vipPrice == null || price == null || vipPrice.compareTo(price) <= 0) {
            throw new IllegalArgumentException("Il prezzo del biglietto VIP deve essere superiore al prezzo del biglietto Standard.");
        }
    }
}
