package com.academy.eventhub.controller;

import com.academy.eventhub.dto.RegisterRequest;
import com.academy.eventhub.entity.User;
import com.academy.eventhub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth") // rotta base per l'autenticazione
public class AuthController
{
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }


    // Punto 8 dello Step 2: Endpoint per gestire la registrazione di un nuovo utente
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody RegisterRequest registerRequest)
    {
        // Chiamata servizio passando i dati estratti dal DTO
        User registeredUser = authService.register(registerRequest.getUsername(), registerRequest.getPassword());

        // Restituizione utente appena creato con lo stato HTTP 201 Created (best practice REST)
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }
}
