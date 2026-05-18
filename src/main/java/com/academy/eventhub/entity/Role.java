package com.academy.eventhub.entity;

import jakarta.persistence.*;

@Entity
/*
    È necessario nominare la table 'authorities'
    poiché è il nome della tabella standard cercato da Spring Security
 */
@Table (name = "authorities")
public class Role
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    /*
      Stringa che si collega direttamente allo username della tabella users.
      Viene usata da JdbcUserDetailsManager per capire a quale utente appartiene il ruolo.
     */
    @Column(nullable = false)
    private String username;

    /*
        Contiene il ruolo effettivo. Esempi: ROLE_USER, ROLE_ORGANIZER, ROLE_ADMIN
     */
    @Column(nullable = false)
    private String authority;

    // Costruttore vuoto per JPA
    public Role()
    {}

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

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}
