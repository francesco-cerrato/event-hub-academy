package com.academy.eventhub.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Tag
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /*
        Dall'altro lato, nell'entity Event ossia l'entità proprietaria,
        è codificata la relazione @ManyToMany. Dunque, in questa entity
        (tag) è necessario solo usare l'attributo "mappedBy"
     */

    @ManyToMany(mappedBy = "tags")
    private Set<Event> events = new HashSet<>(); // Inizializzato per evitare NullPointerException;

    public Tag()
    {}

    public Tag(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }
}
