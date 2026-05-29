package com.academy.eventhub.controller;

import com.academy.eventhub.dto.UserResponseDto;
import com.academy.eventhub.dto.UserUpdateDto;
import com.academy.eventhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8081")
@Tag(name = "Users", description = "Endpoint utente per la consultazione e la gestione " +
        "delle informazioni anagrafiche degli account registrati.")
public class UserController
{
    private final UserService userService;

    @Autowired
    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @Operation(
            summary = "Recupera il profilo dell'utente attualmente autenticato",
            description = "Consente al client di ottenere le informazioni anagrafiche, l'ID e il " +
                    "ruolo dell'utente correntemente loggato basandosi sulle " +
                    "credenziali fornite nell'header Authorization."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informazioni dell'utente corrente restituite con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato o credenziali non valide")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Principal principal)
    {
        // Principal contiene lo username dell'utente attualmente loggato tramite Basic Auth
        String username = principal.getName();

        // Recupera l'utente usando lo username tramite il servizio dedicato
        UserResponseDto currentUser = userService.getUserByUsername(username);
        return ResponseEntity.ok(currentUser);
    }
}
