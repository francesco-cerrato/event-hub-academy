package com.academy.eventhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateDto
{
    @NotBlank(message = "Lo username non può essere vuoto")
    @Size (min = 4, max = 50, message = "Lo username deve essere tra 4 e 50 caratteri")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
