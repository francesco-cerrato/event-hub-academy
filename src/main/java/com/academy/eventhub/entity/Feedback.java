package com.academy.eventhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/*
    Entità che mappa la tabella "feedbacks" nel database.
    Gestisce le recensioni lasciate dagli utenti agli eventi conclusi.
*/
@Entity
@Table(name = "feedbacks",
        uniqueConstraints = {
                // REGOLA DI BUSINESS (Punto 7): Impedisce recensioni duplicate.
                // La combinazione di un singolo utente e un singolo evento deve essere unica nel DB.
                @UniqueConstraint(columnNames = {"user_id", "event_id"})
        })
public class Feedback
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id") // Specifica il nome della chiave primaria sul database
    private Long id;

    @NotNull(message = "Il rating è obbligatorio")
    @Min(value = 1, message = "Il rating minimo consentito è 1")
    @Max(value = 5, message = "Il rating massimo consentito è 5")
    @Column(nullable = false)
    private Integer rating;

    // REGOLA DI BUSINESS (Punto 3): Commento testuale di tipo TEXT per supportare testi lunghi
    @Column(columnDefinition = "TEXT")
    private String comment;


    // Relazione Many-To-One lato Feedback: Molti feedback appartengono a un solo Utente
    // FetchType.LAZY evita di caricare i dati dell'utente a meno che non venga invocato esplicitamente .getUser()
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // Definizione della chiave esterna
    private User user;

    // Relazione Many-To-One lato Feedback: Molti feedback appartengono a un solo Evento
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;


    // Costruttore vuoto per specifiche JPA
    public Feedback()
    {}

    public Feedback(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
}
