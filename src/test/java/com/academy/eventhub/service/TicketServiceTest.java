
package com.academy.eventhub.service;


import com.academy.eventhub.dto.TicketRequestDto;
import com.academy.eventhub.dto.TicketResponseDto;
import com.academy.eventhub.entity.*;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.EventRepository;
import com.academy.eventhub.repository.TicketRepository;
import com.academy.eventhub.repository.UserRepository;

// Import per JUnit (framework di testing)
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Import per Mockito (serve a simulare oggetti)
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

// Import delle funzioni di test (assert)
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

// Dice a JUnit di usare Mockito per creare i mock
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    // Creiamo delle "finte" versioni (mock) dei repository
    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventService eventService;

    // Questa è la classe che vogliamo testare
    // I mock sopra verranno automaticamente "iniettati" dentro
    @InjectMocks
    private TicketServiceImpl ticketService;

    // TEST 1
    @Test
    @DisplayName("Prenotazione biglietto STANDARD con successo se tutti i requisiti sono validi")
    void createTicket_Success() {

        // Dati di input
        Long eventId = 1L;
        String username = "user_demo";

        // Creiamo un utente finto
        User user = new User();
        user.setId(5L);
        user.setUsername(username);

        // Creiamo un evento futuro
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Concerto Live");
        event.setEventDate(LocalDateTime.now().plusDays(10)); // evento tra 10 giorni
        event.setPrice(BigDecimal.valueOf(25.0));
        event.setVipPrice(BigDecimal.valueOf(60.0));

        // DTO di richiesta (tipo STANDARD)
        TicketRequestDto requestDto = new TicketRequestDto();
        requestDto.setType(TicketType.STANDARD);

        // Ticket che simula quello salvato nel database
        Ticket savedTicket = new Ticket();
        savedTicket.setId(100L);
        savedTicket.setUser(user);
        savedTicket.setEvent(event);
        savedTicket.setType(TicketType.STANDARD);
        savedTicket.setPricePaid(BigDecimal.valueOf(25.0));
        savedTicket.setStatus(TicketStatus.ACTIVE);

        // MOCK BEHAVIOR
        // Simuliamo cosa restituiscono i repository

        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // L'utente NON ha già un biglietto
        Mockito.when(ticketRepository.existsByUserIdAndEventIdAndStatus(5L, eventId, TicketStatus.ACTIVE))
                .thenReturn(false);

        // Ci sono posti disponibili
        Mockito.when(eventService.getAvailableSeats(eventId)).thenReturn(50);

        // Quando salviamo, restituiamo il ticket creato sopra
        Mockito.when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // ESECUZIONE
        TicketResponseDto response = ticketService.createTicket(eventId, requestDto, username);

        // VERIFICHE
        assertNotNull(response); // il risultato non deve essere null
        assertEquals(100L, response.getId()); // id corretto
        assertEquals(BigDecimal.valueOf(25.0), response.getPricePaid()); // prezzo corretto
        assertEquals(TicketStatus.ACTIVE, response.getStatus()); // stato corretto

        // Verifica che il save sia stato chiamato UNA volta
        Mockito.verify(ticketRepository, Mockito.times(1)).save(any(Ticket.class));
    }

    // TEST 2
    @Test
    @DisplayName("La prenotazione fallisce se l'evento è nel passato")
    void createTicket_ThrowsException_WhenEventInPast() {

        Long eventId = 1L;
        String username = "user_demo";

        User user = new User();
        user.setUsername(username);

        // Evento già passato
        Event pastEvent = new Event();
        pastEvent.setId(eventId);
        pastEvent.setEventDate(LocalDateTime.now().minusDays(2));

        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(pastEvent));
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Ci aspettiamo un errore
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ticketService.createTicket(eventId, new TicketRequestDto(), username);
        });

        // Controlliamo il messaggio dell'errore
        assertEquals("Impossibile prenotare: l'evento è già iniziato o si è concluso.", exception.getMessage());

        // Verifichiamo che NON venga salvato nulla
        Mockito.verify(ticketRepository, Mockito.never()).save(any(Ticket.class));
    }

    // TEST 3
    @Test
    @DisplayName("La prenotazione fallisce se l'utente ha già un biglietto attivo")
    void createTicket_ThrowsException_WhenDoubleBooking() {

        Long eventId = 1L;
        String username = "user_demo";

        User user = new User();
        user.setId(5L);
        user.setUsername(username);

        Event event = new Event();
        event.setId(eventId);
        event.setEventDate(LocalDateTime.now().plusDays(3));

        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // L'utente HA già un biglietto
        Mockito.when(ticketRepository.existsByUserIdAndEventIdAndStatus(5L, eventId, TicketStatus.ACTIVE))
                .thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ticketService.createTicket(eventId, new TicketRequestDto(), username);
        });

        assertEquals("Hai già una prenotazione attiva per questo evento. Non puoi prenotare due volte.",
                exception.getMessage());

        Mockito.verify(ticketRepository, Mockito.never()).save(any(Ticket.class));
    }

    // TEST 4
    @Test
    @DisplayName("La prenotazione fallisce se i posti della sede sono esauriti")
    void createTicket_ThrowsException_WhenNoSeatsLeft() {

        Long eventId = 1L;
        String username = "user_demo";

        User user = new User();
        user.setId(5L);
        user.setUsername(username);

        Event event = new Event();
        event.setId(eventId);
        event.setEventDate(LocalDateTime.now().plusDays(3));

        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Mockito.when(ticketRepository.existsByUserIdAndEventIdAndStatus(5L, eventId, TicketStatus.ACTIVE))
                .thenReturn(false);

        // ZERO posti disponibili
        Mockito.when(eventService.getAvailableSeats(eventId)).thenReturn(0);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ticketService.createTicket(eventId, new TicketRequestDto(), username);
        });

        assertEquals("Impossibile prenotare: i posti per questo evento sono esauriti.",
                exception.getMessage());

        Mockito.verify(ticketRepository, Mockito.never()).save(any(Ticket.class));
    }

    // TEST 5
    @Test
    @DisplayName("Cancellazione imposta lo stato su CANCELLED ed esegue l'eliminazione logica")
    void deleteTicket_Success() {

        Long ticketId = 50L;
        String username = "owner_user";

        User user = new User();
        user.setUsername(username);

        Event event = new Event();
        event.setEventDate(LocalDateTime.now().plusDays(1));

        // Ticket esistente
        Ticket existingTicket = new Ticket();
        existingTicket.setId(ticketId);
        existingTicket.setUser(user);
        existingTicket.setEvent(event);
        existingTicket.setStatus(TicketStatus.ACTIVE);

        Mockito.when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(existingTicket));

        // Eseguiamo la cancellazione
        ticketService.deleteTicket(ticketId, username);

        // Verifichiamo che lo stato sia cambiato
        assertEquals(TicketStatus.CANCELLED, existingTicket.getStatus());

        // Verifichiamo che sia stato salvato
        Mockito.verify(ticketRepository, Mockito.times(1)).save(existingTicket);
    }
}