package com.academy.eventhub.service;

import com.academy.eventhub.dto.EventRequestDto;
import com.academy.eventhub.dto.EventResponseDto;
import com.academy.eventhub.entity.Event;
import com.academy.eventhub.entity.TicketStatus;
import com.academy.eventhub.entity.User;
import com.academy.eventhub.entity.Venue;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

// Gli "assert" servono a verificare se il risultato è quello atteso (es. se due valori sono uguali)
import static org.junit.jupiter.api.Assertions.*;
// Serve per dire a Mockito di accettare "qualsiasi oggetto" come parametro nei metodi simulati
import static org.mockito.ArgumentMatchers.any;

/*
    @ExtendWith(MockitoExtension.class) dice a JUnit di usare Mockito.
    Questo ci permette di fare test isolati e veloci (millisecondi) senza avviare il database
    reale PostgreSQL e senza caricare tutta l'applicazione Spring Boot.
*/
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    /*
        @Mock crea una copia "finta" (un simulacro) dei repository.
        Non toccheranno il database. Saremo noi a decidere cosa risponderanno quando vengono chiamati.
    */
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VenueRepository venueRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private SpeakerRepository speakerRepository;
    @Mock
    private TicketRepository ticketRepository;

    /*
        @InjectMocks crea l'istanza REALE del servizio che vogliamo testare (EventServiceImpl)
        e gli inserisce dentro automaticamente tutti i repository finti (Mock) dichiarati sopra.
    */
    @InjectMocks
    private EventServiceImpl eventService;

    // TEST 1: CREAZIONE EVENTO (HAPPY PATH)
    @Test // Dice a JUnit che questo metodo è un test da eseguire
    @DisplayName("Creazione evento con successo (Happy Path)") // È il titolo del test visibile nei report
    void createEvent_Success() {

        // 1. ARRANGE (PREPARAZIONE DEI DATI FINTI)
        String username = "organizer_demo";
        User organizer = new User();
        organizer.setUsername(username);

        Venue venue = new Venue();
        venue.setId(1L);
        venue.setName("Auditorium");
        venue.setCapacity(100);

        // Prepariamo l'input che un utente invierebbe tramite form (il DTO)
        EventRequestDto requestDto = new EventRequestDto();
        requestDto.setTitle("Spring Boot Test Workshop");
        requestDto.setVenueId(1L);
        requestDto.setEventDate(LocalDateTime.now().plusDays(5)); // Data tra 5 giorni

        // FISSARE I PREZZI: valorizziamo i campi per superare la validazione del servizio!
        requestDto.setPrice(BigDecimal.valueOf(10.0));
        requestDto.setVipPrice(BigDecimal.valueOf(30.0));

        // Prepariamo l'oggetto Evento che simulerà di essere stato salvato nel DB
        Event savedEvent = new Event("Spring Boot Test Workshop", "Desc", requestDto.getEventDate(), BigDecimal.valueOf(10.0), BigDecimal.valueOf(30.0));
        savedEvent.setId(10L); // Gli assegniamo un ID finto come farebbe il database vero
        savedEvent.setOrganizer(organizer);
        savedEvent.setVenue(venue);
        savedEvent.setTags(Collections.emptySet());
        savedEvent.setSpeakers(Collections.emptySet());

        /*
            ISTRUZIONI PER I MOCK (Mockito.when ... thenReturn):
            Spieghiamo ai repository finti come comportarsi se il Service li chiama.
        */
        // "Quando cerchi l'utente nel database tramite username, restituisci l'organizzatore finto"
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(organizer));

        // "Quando cerchi la sede con ID 1, restituisci la sede finta"
        Mockito.when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));

        // "Quando chiami il metodo .save() passandogli un evento qualsiasi, restituisci l'evento salvato finto con ID 10"
        Mockito.when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        // 2. ACT (AZIONE: ESEGUIAMO IL METODO REALE SOTTO TEST)
        // Chiamiamo il metodo del Service passando il DTO di input e il nome utente
        EventResponseDto response = eventService.createEvent(requestDto, username);

        // 3. ASSERT (VERIFICA DEI RISULTATI)
        assertNotNull(response); // Verifichiamo che la risposta non sia vuota (null)
        assertEquals(10L, response.getId()); // Verifichiamo che l'ID nella risposta sia proprio 10L
        assertEquals("Spring Boot Test Workshop", response.getTitle()); // Verifichiamo che il titolo sia corretto

        // Verifica di sicurezza: controlla che il metodo .save() del DB sia stato chiamato esattamente 1 volta
        Mockito.verify(eventRepository, Mockito.times(1)).save(any(Event.class));
    }


    // TEST 2 (AGGIORNATO)
    // Inserito il mock di Spring Security per evitare NullPointerException
    @Test
    @DisplayName("Aggiornamento fallisce se l'utente non è il creatore dell'evento e non è ADMIN (Regola 7)")
    void updateEvent_ThrowsAccessDenied_WhenUserIsNotOrganizer() {

        // 1. ARRANGE
        Long eventId = 1L;
        String realOrganizer = "organizer_vero";
        String maliciousUser = "utente_malizioso";

        User organizer = new User();
        organizer.setUsername(realOrganizer);

        Event existingEvent = new Event();
        existingEvent.setId(eventId);
        existingEvent.setOrganizer(organizer);

        existingEvent.setTags(new java.util.HashSet<>());
        existingEvent.setSpeakers(new java.util.HashSet<>());

        EventRequestDto requestDto = new EventRequestDto();

        // MOCK DI SPRING SECURITY: Diciamo al test che l'utente attuale è un semplice ROLE_USER (non ADMIN)
        org.springframework.security.core.Authentication authentication = Mockito.mock(org.springframework.security.core.Authentication.class);
        org.springframework.security.core.context.SecurityContext securityContext = Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

        java.util.List<org.springframework.security.core.GrantedAuthority> authorities =
                java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"));
        Mockito.doReturn(authorities).when(authentication).getAuthorities();

        // Configuriamo il mock del repository
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));

        // 2. ACT & 3. ASSERT
        assertThrows(AccessDeniedException.class, () -> {
            eventService.updateEvent(eventId, requestDto, maliciousUser);
        });

        // Verifica di sicurezza finale
        Mockito.verify(eventRepository, Mockito.never()).save(any(Event.class));
    }

    // TEST 3: CANCELLAZIONE SE EVENTO NON ESISTE

    @Test
    @DisplayName("Cancellazione fallisce se l'evento non esiste")
    void deleteEvent_ThrowsResourceNotFound_WhenEventNotExists() {

        // 1. ARRANGE
        Long eventId = 99L; // Un ID che non esiste nel sistema

        // Configuriamo il mock: "Quando cerchi l'evento 99, rispondi con un Optional vuoto (non trovato)"
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // 2. ACT & 3. ASSERT
        // Verifichiamo che il metodo lanci la tua eccezione custom "ResourceNotFoundException"
        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.deleteEvent(eventId, "any_user");
        });

        // Verifichiamo che il metodo .delete() del DB non sia mai stato chiamato
        Mockito.verify(eventRepository, Mockito.never()).delete(any(Event.class));
    }

    // TEST 4: CALCOLO MATEMATICO POSTI

    @Test
    @DisplayName("Calcolo accurato dei posti disponibili in base alla capienza della sede")
    void getAvailableSeats_Success() {

        // 1. ARRANGE
        Long eventId = 1L;

        // Creiamo una sede finta che ha una capienza massima di 150 posti
        Venue venue = new Venue();
        venue.setCapacity(150);

        Event event = new Event();
        event.setId(eventId);
        event.setVenue(venue);

        // Diciamo a Mockito cosa rispondere
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Simuliamo che nel database ci siano già 40 biglietti attivi (venduti) per questo evento
        Mockito.when(ticketRepository.countByEventIdAndStatus(eventId, TicketStatus.ACTIVE)).thenReturn(40L);

        // 2. ACT
        // Eseguiamo il calcolo matematico reale scritto nella tua classe EventServiceImpl
        int availableSeats = eventService.getAvailableSeats(eventId);

        // 3. ASSERT
        // Verifichiamo che il risultato dell'operazione matematica sia esattamente 110 (ovvero 150 - 40)
        assertEquals(110, availableSeats);
    }

    // TEST 5: VALIDAZIONE PREZZI
    @Test
    @DisplayName("Creazione evento fallisce se il prezzo VIP è minore o uguale allo Standard")
    void createEvent_ThrowsIllegalArgument_WhenVipPriceInvalid() {

        // 1. ARRANGE
        String username = "organizer_demo";
        User organizer = new User();
        organizer.setUsername(username);

        // Prepariamo un input non valido: VIP (30.0) costa meno dello Standard (50.0)
        EventRequestDto requestDto = new EventRequestDto();
        requestDto.setTitle("Workshop Autunno");
        requestDto.setPrice(BigDecimal.valueOf(50.0));
        requestDto.setVipPrice(BigDecimal.valueOf(30.0));

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(organizer));

        // 2. ACT & 3. ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(requestDto, username);
        });

        assertEquals("Il prezzo del biglietto VIP deve essere superiore al prezzo del biglietto Standard.", exception.getMessage());

        // Verifichiamo che l'esecuzione si sia bloccata prima di toccare la persistenza
        Mockito.verify(eventRepository, Mockito.never()).save(any(Event.class));
    }

    // TEST 6: MODERAZIONE ADMIN
    @Test
    @DisplayName("Aggiornamento ha successo se l'utente è ADMIN anche se non ha creato l'evento")
    void updateEvent_Success_WhenUserIsAdmin() {

        // 1. ARRANGE
        Long eventId = 1L;
        String adminUser = "admin_super";
        String creatorUser = "organizer_vecchio";

        User organizer = new User();
        organizer.setUsername(creatorUser);

        Venue venue = new Venue();
        venue.setId(2L);

        Event existingEvent = new Event("Vecchio Titolo", "Desc", LocalDateTime.now().plusDays(2), BigDecimal.valueOf(10.0), BigDecimal.valueOf(20.0));
        existingEvent.setId(eventId);
        existingEvent.setOrganizer(organizer);

        EventRequestDto requestDto = new EventRequestDto();
        requestDto.setTitle("Nuovo Titolo Modificato dall'Admin");
        requestDto.setVenueId(2L);
        requestDto.setPrice(BigDecimal.valueOf(15.0));
        requestDto.setVipPrice(BigDecimal.valueOf(40.0)); // Valido (VIP > Standard)



        // MOCK DI SPRING SECURITY: Configuriamo l'utente nel contesto come ROLE_ADMIN
        org.springframework.security.core.Authentication authentication = Mockito.mock(org.springframework.security.core.Authentication.class);
        org.springframework.security.core.context.SecurityContext securityContext = Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

        java.util.List<org.springframework.security.core.GrantedAuthority> authorities =
                java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"));
        Mockito.doReturn(authorities).when(authentication).getAuthorities();

        // Comportamento dei Mock
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        Mockito.when(venueRepository.findById(2L)).thenReturn(Optional.of(venue));

        // 2. ACT
        EventResponseDto response = eventService.updateEvent(eventId, requestDto, adminUser);

        // 3. ASSERT
        assertNotNull(response);
        assertEquals("Nuovo Titolo Modificato dall'Admin", response.getTitle());

        // Verifichiamo che il save esplicito NON sia stato chiamato grazie al Dirty Checking transazionale
        Mockito.verify(eventRepository, Mockito.never()).save(any(Event.class));
    }
}
