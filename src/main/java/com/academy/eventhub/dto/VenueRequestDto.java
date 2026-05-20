package com.academy.eventhub.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
    Data Transfer Object (DTO) per l'acquisizione dei dati di una sede (Venue).
    Viene utilizzato per gestire in sicurezza l'input proveniente dalle richieste HTTP
    sia in fase di creazione (POST) che di aggiornamento (PUT).

    Include l'applicazione della Bean Validation per garantire l'integrità dei dati
    prima che questi raggiungano lo strato di business (Service).
 */

public class VenueRequestDto
{
    @NotBlank(message = "Il nome della sede è obbligatorio")
    private String name;

    @NotBlank(message = "L'indirizzo della sede è obbligatorio")
    private String address;

    @NotNull
    @Min(value = 1, message = "La capienza deve essere di almeno 1 posto")
    private int capacity;

    public VenueRequestDto()
    {}

    public VenueRequestDto(String name, String address, int capacity) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
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
