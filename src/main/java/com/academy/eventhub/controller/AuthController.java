package com.academy.eventhub.controller;

import com.academy.eventhub.dto.ProfileResponseDto;
import com.academy.eventhub.dto.RegisterRequest;
import com.academy.eventhub.dto.UserResponseDto;
import com.academy.eventhub.entity.Profile;
import com.academy.eventhub.entity.User;
import com.academy.eventhub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth") // rotta base per l'autenticazione
@Tag(name = "Authentication", description = "Endpoint per la registrazione di nuovi utenti nel sistema")
public class AuthController
{
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }


    // Punto 8 dello Step 2: Endpoint per gestire la registrazione di un nuovo utente
    @Operation(
            summary = "Registra un nuovo utente",
            description = "Crea un nuovo account nel sistema (USER, ORGANIZER O ADMIN) applicando l'algoritmo di cifratura BCrypt sulla password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utente registrato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi o username già esistente")
    })
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody RegisterRequest registerRequest)
    {
        // Chiamata servizio passando i dati estratti dal DTO
        User registeredUser = authService.register(registerRequest);

        UserResponseDto responseDto = convertToResponseDto(registeredUser);

        // Restituizione utente appena creato con lo stato HTTP 201 Created (best practice REST)
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    UserResponseDto convertToResponseDto(User registeredUser)
    {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(registeredUser.getId());
        userResponseDto.setUsername(registeredUser.getUsername());

        // Mappatura sicura del profilo per evitare ricorsione ciclica
        if (registeredUser.getProfile() != null) {
            Profile profileEntity = registeredUser.getProfile();
            ProfileResponseDto profileDto = new ProfileResponseDto();
            profileDto.setId(profileEntity.getId());
            profileDto.setFirstName(profileEntity.getFirstName());
            profileDto.setLastName(profileEntity.getLastName());
            profileDto.setBio(profileEntity.getBio());
            profileDto.setCity(profileEntity.getCity());
            profileDto.setAvatarUrl(profileEntity.getAvatarUrl());

            userResponseDto.setProfile(profileDto);
        }

        return userResponseDto;
    }
}
