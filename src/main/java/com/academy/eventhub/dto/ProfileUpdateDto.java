package com.academy.eventhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileUpdateDto
{
    @NotBlank(message = "Il nome non può essere vuoto")
    private String firstName;
    @NotBlank(message = "Il cognome non può essere vuoto")
    private String lastName;
    @Size(max = 255, message = "La biografia non può superare i 255 caratteri")
    private String bio;
    @NotBlank(message = "La città non può essere vuota")
    private String city;
    @Size(max = 500, message = "L'URL dell'avatar è troppo lungo")
    private String avatarUrl;

    public ProfileUpdateDto()
    {}

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
}
