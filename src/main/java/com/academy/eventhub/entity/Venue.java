package com.academy.eventhub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "venues")
public class Venue
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    /*
        Di default una variabile int non può essere null, diventerebbe 0.
        La variabile oggetto Integer invece potrebbe essere null.
        In ogni caso, impostando il campo database con nullable = false
        non dovrebbero esserci problemi
     */
    @Column(nullable = false)
    private int capacity;

    /*
        mappedBy = "venue": Indica che la relazione è bidirezionale ed è gestita dal campo 'venue' presente nella classe Event.
        cascade = CascadeType.ALL: Qualsiasi operazione (salvataggio, aggiornamento, cancellazione) effettuata su questa entità si riflette automaticamente su tutti gli eventi associati.
        fetch = FetchType.LAZY: Gli eventi associati vengono caricati dal database solo quando si accede esplicitamente alla lista (es. tramite getEvents()), ottimizzando le prestazioni.
     */
    // Questo punterà al campo @ManyToOne di Event chiamato event
    //@OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //private List<Event> events;

    public Venue()
    {}

    public Venue(String name, String address, int capacity) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
