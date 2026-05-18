package com.academy.eventhub.entity;

import jakarta.persistence.*;

@Entity
@Table (name = "profiles")
public class Profile
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "profile_id")
    private Long id;

    /*
        Hibernate automaticamente traduce, sul DB, il camelCase in snake_case
     */
    private String firstName;

    private String lastName;

    private String bio;

    private String city;

    /*
        Gli URL possono essere molto lunghi.
        È buona norma impostare length = 500 (o superiore) nell'annotazione
        @Column per evitare errori di troncamento nel database
        (il default di JPA è 255 caratteri).
     */
    @Column(length = 500)
    private String avatarUrl;

    /*
        Relazione 1-1 proprietaria:
        crea la colonna fisica 'user_id' nella tabella profiles
     */
    @OneToOne
    @JoinColumn (name = "user_id", nullable = false, unique = true)
    private User user;


    // Costruttore vuoto per JPA
    public Profile()
    {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", bio='" + bio + '\'' +
                ", city='" + city + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
