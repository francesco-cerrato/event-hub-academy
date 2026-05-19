package com.academy.eventhub.dto;

import com.academy.eventhub.entity.Profile;

import java.util.Set;

/*
    Data Transfer Object (DTO) per la risposta dei dati User.
    Utilizzato per veicolare le informazioni dell'utente verso il client,
    celando dati sensibili come la password
 */
public class UserResponseDto
{
    private Long id;
    private String username;
    // Contiene solo i nomi dei ruoli in formato stringa (Es. "ROLE_USER")
    private Set<String> roles;
    /*
        La password va nascosta quando si restituisce in output i dati di uno user
     */
    //private String password;


    // Costruttore vuoto per Jackson (deserializzazione JSON)
    public UserResponseDto()
    {}

    public UserResponseDto(Long id, String username, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
