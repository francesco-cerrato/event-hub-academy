package com.academy.eventhub.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



@Entity
public class Event
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Prezzo del biglietto VIP
    @Column(name = "vip_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal vipPrice;


    /*
        Dall'altro lato, nell'entity tag ossia l'entità inversa,
        è codificata la relazione @ManyToMany.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "event_tags",  // Nome della tabella di join
            joinColumns = @JoinColumn( name = "event_id"), // Chiave esterna dell'entità corrente
            inverseJoinColumns = @JoinColumn( name = "tag_id") // Chiave esterna dell'altra entità
    )
    private Set<Tag> tags = new HashSet<>();


    // Relazione ManyToOne con l'utente organizzatore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    // Relazione ManyToOne con la sede dell'evento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    /*
        Dall'altro lato, nell'entity Speaker ossia l'entità inversa,
        è codificata la relazione @ManyToMany.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "event_speakers",  // Nome della tabella di join
            joinColumns = @JoinColumn( name = "event_id"), // Chiave esterna dell'entità corrente
            inverseJoinColumns = @JoinColumn( name = "speaker_id") // Chiave esterna dell'altra entità
    )
    private Set<Speaker> speakers = new HashSet<>();

    // Richiesto per lo Step 9: Feedback ricevuti dall'evento
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();

    // Richiesto per lo Step 7 e 8: Biglietti staccati per l'evento
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();


    public Event()
    {}

    public Event(String title, String description, LocalDateTime eventDate, BigDecimal price, BigDecimal vipPrice) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.price = price;
        this.vipPrice = vipPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(BigDecimal vipPrice) {
        this.vipPrice = vipPrice;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Set<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(Set<Speaker> speakers) {
        this.speakers = speakers;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
