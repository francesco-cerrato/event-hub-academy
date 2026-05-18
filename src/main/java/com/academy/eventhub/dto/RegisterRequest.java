package com.academy.eventhub.dto;

/*
    Data Transfer Object (DTO) per la richiesta di registrazione.
    Un DTO è un oggetto privo di logica di business utilizzato esclusivamente per
    trasportare i dati tra il client (es. Postman/Frontend) e il server.
    Separa le entità del database (User) dal livello API per motivi di sicurezza,
    flessibilità e validazione dei dati in ingresso.
 */


public class RegisterRequest
{
    private String username;
    private String password;


    /*
        Costruttore vuoto per Jackson
        obbligatorio per consentire a Spring (Jackson) di deserializzare
        correttamente il file JSON inviato da Postman
     */
    public RegisterRequest()
    {}


    public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
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
}
