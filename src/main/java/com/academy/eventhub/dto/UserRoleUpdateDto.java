package com.academy.eventhub.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRoleUpdateDto
{
    @NotBlank(message = "Il ruolo è obbligatorio")
    private String role; //Es. "ROLE_ADMIN" e "ROLE_USER"

    public UserRoleUpdateDto()
    {}

    public UserRoleUpdateDto(String role)
    {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
