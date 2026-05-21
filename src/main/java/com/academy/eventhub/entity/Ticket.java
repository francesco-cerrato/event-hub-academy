package com.academy.eventhub.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Ticket
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    /*
        In questo caso, la relazione @ManyToOne è unidirezionale.
        Il codice di relazione è scritto esclusivamente nell'entity Ticket
        (non nell'entity user)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private BigDecimal pricePaid;


    // Impedisce l'inserimento di testi errati (es. "StAndard" o "Vip-Pass") nel database o nel codice.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketType type; // Utilizza l'enum TicketType

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;  // Utilizza l'enum TicketStatus

    private LocalDateTime createdAt = LocalDateTime.now();

    public Ticket()
    {}

    public Ticket(Long id, User user, Event event, BigDecimal pricePaid, TicketType type, TicketStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.pricePaid = pricePaid;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    /*
        Alla creazione di un nuovo biglietto l'id è generato automaticamente,
        createdAt ha già il valore di default ossia LocalDateTime.now.
        Dunque conviene avere un costruttore senza questa due campi
     */
    public Ticket(User user, Event event, BigDecimal pricePaid, TicketType type, TicketStatus status) {
        this.user = user;
        this.event = event;
        this.pricePaid = pricePaid;
        this.type = type;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public BigDecimal getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(BigDecimal pricePaid) {
        this.pricePaid = pricePaid;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
