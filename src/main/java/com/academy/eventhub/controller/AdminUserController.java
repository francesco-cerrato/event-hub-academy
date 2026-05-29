package com.academy.eventhub.controller;

import com.academy.eventhub.dto.UserResponseDto;
import com.academy.eventhub.dto.UserRoleUpdateDto;
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

import java.util.List;

@RestController
@RequestMapping("/admin/users") // Bloccato automaticamente da Spring Security
@Tag(name = "Admin Users", description = "Endpoint di amministrazione per la gestione degli utenti " +
        "e dei privilegi del sistema. Accesso limitato esclusivamente agli utenti con ruolo ADMIN.")
public class AdminUserController
{
    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService)
    {
        this.userService = userService;
    }

    @Operation(
            summary = "Ottiene l'elenco di tutti gli utenti",
            description = "Recupera una lista completa di tutti gli account utente registrati sulla piattaforma. Riservato all'amministratore."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Elenco degli utenti restituito con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesti i permessi di ADMIN")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers()
    {
        List<UserResponseDto> foundUsers = userService.getAllUsers();
        return ResponseEntity.ok(foundUsers);
    }

    @Operation(
            summary = "Recupera un utente tramite ID",
            description = "Consente di visualizzare le informazioni di un singolo utente cercandolo attraverso il suo ID unico. Riservato all'amministratore."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente trovato e restituito con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesti i permessi di ADMIN"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato con l'ID fornito")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id)
    {
        UserResponseDto foundUser = userService.getUserById(id);
        return ResponseEntity.ok(foundUser);
    }

    @Operation(
            summary = "Aggiorna le informazioni di un utente",
            description = "Consente di modificare le credenziali o i dati di base associati a un account utente esistente. Riservato all'amministratore."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente aggiornato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi o non conformi"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesti i permessi di ADMIN"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato con l'ID fornito")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto inputDto)
    {
        UserResponseDto updatedUser = userService.updateUser(id, inputDto);
        return ResponseEntity.ok(updatedUser);
    }


    @Operation(
            summary = "Elimina un utente dal sistema",
            description = "Rimuove permanentemente l'account utente selezionato tramite il suo ID. Riservato all'amministratore."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utente eliminato con successo (No Content)"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesti i permessi di ADMIN"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato con l'ID fornito")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id)
    {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }







    @Operation(
            summary = "Aggiorna il ruolo di un utente",
            description = "Consente a un amministratore di modificare i permessi e i ruoli assegnati a un utente " +
                    "specifico nel sistema (es. promuovere da USER a ORGANIZER o ADMIN)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruolo dell'utente aggiornato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi o formato del ruolo errato"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesti i permessi di ADMIN"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato con l'ID fornito")
    })
    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(@PathVariable Long id, @Valid @RequestBody UserRoleUpdateDto roleDto)
    {
        UserResponseDto updatedUser = userService.updateUserRole(id, roleDto);
        return ResponseEntity.ok(updatedUser);
    }
}
