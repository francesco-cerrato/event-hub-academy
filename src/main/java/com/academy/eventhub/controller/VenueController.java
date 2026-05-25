package com.academy.eventhub.controller;

import com.academy.eventhub.dto.VenueRequestDto;
import com.academy.eventhub.dto.VenueResponseDto;
import com.academy.eventhub.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*
    Nel SecurityConfig è stato configurato un accesso limitato
    alla rotta "admin" esclusivamente agli utenti di ruolo ADMIN
    Codice: .requestMatchers("/admin/**").hasRole("ADMIN")
 */
@RequestMapping("/admin/venues")
@RestController
@Tag(name = "Admin Venues", description = "Endpoint di amministrazione per la gestione " +
        "delle sedi degli eventi. Accesso limitato esclusivamente agli utenti con ruolo ADMIN.")
public class VenueController
{

    private final VenueService venueService;

    @Autowired
    public VenueController(VenueService venueService)
    {
        this.venueService = venueService;
    }


    @Operation(
            summary = "Crea una nuova sede",
            description = "Consente a un amministratore di registrare una nuova sede o locale nel sistema compilando i dati anagrafici e di capacità."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sede creata con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi o non conformi"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: permessi ADMIN richiesti")
    })
    @PostMapping
    public ResponseEntity<VenueResponseDto> createVenue(@Valid @RequestBody VenueRequestDto venueRequestDto)
    {
        VenueResponseDto createdVenue = venueService.createVenue(venueRequestDto);
        return new ResponseEntity<>(createdVenue, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Recupera una sede tramite ID",
            description = "Consente di visualizzare i dettagli completi di una specifica sede tramite il suo identificativo unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sede trovata e restituita con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: permessi ADMIN richiesti"),
            @ApiResponse(responseCode = "404", description = "Sede non trovata con l'ID fornito")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDto> getVenueById(@PathVariable Long id)
    {
        VenueResponseDto foundVenue = venueService.getVenueById(id);
        return ResponseEntity.ok(foundVenue);
    }

    @Operation(
            summary = "Ottiene la lista di tutte le sedi",
            description = "Recupera l'elenco completo di tutte le sedi registrate all'interno del sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Elenco delle sedi restituito con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: permessi ADMIN richiesti")
    })
    @GetMapping
    public ResponseEntity<List<VenueResponseDto>> getAllVenues()
    {
        List<VenueResponseDto> venueList = venueService.getAllVenues();
        return ResponseEntity.ok(venueList);
    }

    @Operation(
            summary = "Aggiorna una sede esistente",
            description = "Modifica i dettagli e le informazioni di una sede già registrata a sistema tramite il suo ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sede aggiornata con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi o non conformi"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: permessi ADMIN richiesti"),
            @ApiResponse(responseCode = "404", description = "Sede non trovata con l'ID fornito")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VenueResponseDto> updateVenue(@PathVariable Long id,
                                                        @Valid @RequestBody VenueRequestDto venueRequestDto)
    {
        VenueResponseDto updatedVenue = venueService.updateVenue(id, venueRequestDto);
        return  ResponseEntity.ok(updatedVenue);
    }

    @Operation(
            summary = "Elimina una sede dal sistema",
            description = "Rimuove definitivamente una sede tramite il suo identificativo unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sede eliminata con successo (No Content)"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: permessi ADMIN richiesti"),
            @ApiResponse(responseCode = "404", description = "Sede non trovata con l'ID fornito")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id)
    {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}
