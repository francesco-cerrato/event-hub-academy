package com.academy.eventhub.dto;

/*
    Data Transfer Object (DTO) per la richiesta di registrazione.
    Un DTO è un oggetto privo di logica di business utilizzato esclusivamente per
    trasportare i dati tra il client (es. Postman/Frontend) e il server.
    Separa le entità del database (User) dal livello API per motivi di sicurezza,
    flessibilità e validazione dei dati in ingresso.
 */


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest
{
    @NotBlank(message = "Lo username non può essere vuoto")
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank(message = "La password non può essere vuota")
    private String password;

    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;

    @NotBlank(message = "La città è obbligatoria")
    private String city;

    // In questo RegisterRequest mancano i campi bio e avatarUrl da inserire nel profilo


    /*
        Costruttore vuoto per Jackson
        obbligatorio per consentire a Spring (Jackson) di deserializzare
        correttamente il file JSON inviato da Postman
     */
    public RegisterRequest()
    {}


    public RegisterRequest(String username, String password, String firstName, String lastName, String city) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
