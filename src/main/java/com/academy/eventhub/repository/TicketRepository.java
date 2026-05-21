package com.academy.eventhub.repository;

import com.academy.eventhub.entity.Ticket;
import com.academy.eventhub.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/*
    Questo repository è iniettato all'interno di EventServiceImpl per countByEventIdAndStatus

    Mentre è iniettato in TicketServiceImpl per prenotare e cancellare un ticket (.save e .delete)
 */
public interface TicketRepository extends JpaRepository<Ticket, Long>
{
    // Questo metodo serve per il punto 7 della traccia: contare i ticket attivi per calcolare i posti disponibili
    /*
        Hibernate, scomponendo l'intero nome del metodo (count By, EventId, And, TicketStatus),
        genera automaticamente una query di questo tipo:
        SELECT COUNT(*) FROM Ticket WHERE EventId = ? AND status = ?
     */
    long countByEventIdAndStatus(Long eventId, TicketStatus status);
}
