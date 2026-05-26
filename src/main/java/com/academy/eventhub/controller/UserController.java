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


    @Operation(
            summary = "Ottiene l'elenco di tutti gli utenti",
            description = "Recupera una lista completa di tutti gli account utente registrati sulla piattaforma."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Elenco degli utenti restituito con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers()
    {
        List<UserResponseDto> foundUsers = userService.getAllUsers();
        return ResponseEntity.ok(foundUsers);
    }

    @Operation(
            summary = "Recupera un utente tramite ID",
            description = "Consente di visualizzare le informazioni di un singolo utente cercandolo attraverso il suo ID unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente trovato e restituito con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato con l'ID fornito")
    })
    @GetMapping({"/{id}"})
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id)
    {
        UserResponseDto foundUser = userService.getUserById(id);
        return ResponseEntity.ok(foundUser);
    }

    @Operation(
            summary = "Aggiorna le informazioni di un utente",
            description = "Consente di modificare le credenziali o i dati di base associati a un account utente esistente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente aggiornato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi o non conformi"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato con l'ID fornito")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
                                                      @Valid @RequestBody UserUpdateDto inputDto)
    {
        UserResponseDto updateUser = userService.updateUser(id, inputDto);
        return ResponseEntity.ok(updateUser);
    }

    @Operation(
            summary = "Elimina un utente dal sistema",
            description = "Rimuove permanentemente l'account utente selezionato tramite il suo ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utente eliminato con successo (No Content)"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato con l'ID fornito")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id)
    {
        userService.deleteUser(id);
        // Ritorna uno stato HTTP 204 No Content (operazione riuscita, nessun corpo)
        return ResponseEntity.noContent().build();
    }

}
