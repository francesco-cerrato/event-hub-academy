package com.academy.eventhub.service;

import com.academy.eventhub.dto.TicketRequestDto;
import com.academy.eventhub.dto.TicketResponseDto;
import com.academy.eventhub.entity.*;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.EventRepository;
import com.academy.eventhub.repository.TicketRepository;
import com.academy.eventhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TicketServiceImpl implements TicketService
{
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventService eventService; // Per evitare di duplicare il codice

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository,
                             EventRepository eventRepository, EventService eventService)
    {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    public TicketResponseDto createTicket(Long eventId, TicketRequestDto ticketRequestDto, String currentUsername)
    {
        // Recupero evento
        Event foundEvent = eventRepository.findById(eventId)
                .orElseThrow( () -> new ResourceNotFoundException("Evento non trovato con id: " + eventId ) );

        // Recupero utente loggato
        User foundUser = userRepository.findByUsername(currentUsername)
                .orElseThrow( () -> new ResourceNotFoundException("Utente non trovato con username: " + currentUsername));


        // Controllo che il Ticket da prenotare non appartenga (relazione) ad un evento svolto nel passato. Step 8 punto 1
        if (foundEvent.getEventDate().isBefore(LocalDateTime.now()))
        {
            throw new IllegalStateException("Impossibile prenotare: l'evento è già iniziato o si è concluso.");
        }

        // 4. Controllo doppia prenotazione. Step 8 punto 2
        if (ticketRepository.existsByUserIdAndEventIdAndStatus(foundUser.getId(), eventId, TicketStatus.ACTIVE))
        {
            throw new IllegalStateException("Hai già una prenotazione attiva per questo evento. Non puoi prenotare due volte.");
        }


        // Verifica posti disponibili
        int seatsLeft = eventService.getAvailableSeats(eventId);
        if (seatsLeft <= 0)
        {
            throw new IllegalStateException("Impossibile prenotare: i posti per questo evento sono esauriti.");
        }

        // Calcolo automatico del prezzo
        BigDecimal finalPrice;
        if (ticketRequestDto.getType() == TicketType.VIP)
        {
            finalPrice = foundEvent.getVipPrice();
        }
        else
        {
            finalPrice = foundEvent.getPrice();
        }


        // Creazione e popolamento dell'entità Ticket
        Ticket newTicket = new Ticket();
        newTicket.setUser(foundUser);
        newTicket.setEvent(foundEvent);
        newTicket.setStatus(TicketStatus.ACTIVE);
        newTicket.setType(ticketRequestDto.getType());
        newTicket.setPricePaid(finalPrice);


        // Salvataggio del ticket nel DB
        Ticket savedTicket = ticketRepository.save(newTicket);

        // Conversione in DTO response e return
        return convertToResponseDto(savedTicket);
    }

    @Override
    @Transactional
    public void deleteTicket(Long ticketId, String currentUsername)
    {
        // Recupero del ticket
        Ticket foundTicket = ticketRepository.findById(ticketId)
                .orElseThrow( () -> new ResourceNotFoundException("Ticket non trovato con id: " + ticketId));

        // Controllo di sicurezza: l'utente può cancellare solo le PROPRIE prenotazioni
        if (!foundTicket.getUser().getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("Non sei autorizzato a cancellare questa prenotazione.");
        }

        // Controllo per verificare che l'evento non sia già iniziato (4 regole di business, punto 4)
        if (foundTicket.getEvent().getEventDate().isBefore(LocalDateTime.now()))
        {
            throw new IllegalStateException("Impossibile cancellare la prenotazione: l'evento è già iniziato o si è concluso.");
        }



        // Cancellazione logica impostando lo stato su CANCELLED
        foundTicket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(foundTicket);

        /*
            In questo caso la delete è logica in quanto opera sul campo enum "status" (TicketStatus)
            settandolo su "CANCELLED". L'eliminazione dunque non è fisica sul DB
            L'alternativa dunque sarebbe la seguente:

            ticketRepository.delete(foundTicket)

         */
    }

    private TicketResponseDto convertToResponseDto(Ticket ticket)
    {
        TicketResponseDto ticketResponseDto = new TicketResponseDto();

        ticketResponseDto.setId(ticket.getId());
        ticketResponseDto.setEvent_id(ticket.getEvent().getId());
        ticketResponseDto.setEventTitle(ticket.getEvent().getTitle());
        ticketResponseDto.setUsername(ticket.getUser().getUsername());
        ticketResponseDto.setType(ticket.getType());
        ticketResponseDto.setPricePaid(ticket.getPricePaid());
        ticketResponseDto.setStatus(ticket.getStatus());
        ticketResponseDto.setCreatedAt(ticket.getCreatedAt());

        return ticketResponseDto;
    }
}
