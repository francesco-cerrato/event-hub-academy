package com.academy.eventhub.controller;

import com.academy.eventhub.dto.UserResponseDto;
import com.academy.eventhub.dto.UserRoleUpdateDto;
import com.academy.eventhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
