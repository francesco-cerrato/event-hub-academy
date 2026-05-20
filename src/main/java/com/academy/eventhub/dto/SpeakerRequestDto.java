package com.academy.eventhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SpeakerRequestDto
{
    @NotBlank(message = "Il nome dello speaker è obbligatorio")
    @Size(max = 100, message = "Il nome non può superare i 100 caratteri")
    private String name;

    @NotBlank(message = "La biografia dello speaker è obbligatoria")
    private String bio;

    public SpeakerRequestDto()
    {}

    public SpeakerRequestDto(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
