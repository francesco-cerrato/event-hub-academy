package com.academy.eventhub.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "user_id")
    private Long id;


    /*
        È preferibile evitare di specificare il nome della
        colonna (@Column( name = "username)) poiché il nome del campo
        corrisponde al nome della colonna nel DB
     */
    @Column (nullable = false, unique = true)
    private String username;

    @Column (nullable = false)
    private String password;

    // Richiesto da JdbcUserDetailsManager di Spring Security per attivare/disattivare l'account
    private boolean enabled = true;

    // Regola di business 4.9: Gestione del ban, messaggi di errore dedicati e cancellazione prenotazioni
    private boolean banned = false;


    // Relazione 1-1 inversa: fa riferimento al campo 'user' presente nella classe Profile
    @OneToOne (mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;

    // Relazione 1-N: fa riferimento al campo "organizer" presente nella classe Event
    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> organizedEvents = new ArrayList<>();

    // Costruttore vuoto per JPA
    public User()
    {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Event> getOrganizedEvents() {
        return organizedEvents;
    }

    public void setOrganizedEvents(List<Event> organizedEvents) {
        this.organizedEvents = organizedEvents;
    }

    @Override
    public String toString()
    {
        /*
            Nel metodo toString è necessario evitare la stampa
            della password, anche se è cifrata
         */
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", enabled=" + enabled +
                ", banned=" + banned +
                '}';
    }
}
