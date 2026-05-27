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


    // TEST 2: VINCOLO REGOLA 7

    @Test
    @DisplayName("Aggiornamento fallisce se l'utente non è il creatore dell'evento (Regola 7)")
    void updateEvent_ThrowsAccessDenied_WhenUserIsNotOrganizer() {

        // 1. ARRANGE
        Long eventId = 1L;
        String realOrganizer = "organizer_vero";
        String maliciousUser = "utente_malizioso"; // L'utente che proverà a fare l'attacco

        User organizer = new User();
        organizer.setUsername(realOrganizer);

        // Creiamo l'evento attualmente presente nel database, che appartiene a "organizer_vero"
        Event existingEvent = new Event();
        existingEvent.setId(eventId);
        existingEvent.setOrganizer(organizer);

        EventRequestDto requestDto = new EventRequestDto();

        // Configuriamo il mock: "Quando il service cercherà l'evento da modificare, faglielo trovare"
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));

        // 2. ACT & 3. ASSERT (INSIEME)
        /*
            assertThrows si aspetta che il codice dentro l'espressione lancia un errore.
            Se il codice lancia l'eccezione, il test PASSA. Se il codice non si blocca, il test FALLISCE.
        */
        assertThrows(AccessDeniedException.class, () -> {
            // Chiamiamo il metodo inserendo l'ID dell'evento ma passandogli lo username dell'utente malizioso
            eventService.updateEvent(eventId, requestDto, maliciousUser);
        });

        // Verifica di sicurezza finale: dato che l'utente non era il proprietario,
        // verifichiamo che il metodo .save() sul database NON sia mai stato chiamato (never())
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
}
