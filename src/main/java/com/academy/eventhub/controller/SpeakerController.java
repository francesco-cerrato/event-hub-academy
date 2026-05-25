package com.academy.eventhub.controller;

import com.academy.eventhub.dto.SpeakerRequestDto;
import com.academy.eventhub.dto.SpeakerResponseDto;
import com.academy.eventhub.service.SpeakerService;
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


@RestController
/*
    La regola globale impostata in SecurityConfig consente l'accesso solo agli ADMIN
    .requestMatchers("/admin/**").hasRole("ADMIN")
 */
@RequestMapping("/admin/speakers")
@Tag(name = "Admin Speakers", description = "Endpoint di amministrazione per la gestione " +
        "degli speaker (relatori) del sistema. Accesso limitato esclusivamente agli utenti con ruolo ADMIN.")
public class SpeakerController
{
    private final SpeakerService speakerService;

    @Autowired
    public SpeakerController(SpeakerService speakerService)
    {
        this.speakerService = speakerService;
    }

    @Operation(
            summary = "Crea un nuovo speaker",
            description = "Consente a un amministratore di registrare un nuovo relatore compilando i dati anagrafici, " +
                    "la biografia e le competenze aziendali."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Speaker creato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi o non conformi"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesto il ruolo ADMIN")
    })
    @PostMapping
    public ResponseEntity<SpeakerResponseDto> createSpeaker(@Valid @RequestBody SpeakerRequestDto speakerRequestDto)
    {
        SpeakerResponseDto createdSpeaker = speakerService.createSpeaker(speakerRequestDto);
        return new ResponseEntity<>(createdSpeaker, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Recupera uno speaker tramite ID",
            description = "Consente di visualizzare le informazioni di dettaglio e la " +
                    "scheda anagrafica di un relatore specifico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Speaker trovato e restituito con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesto il ruolo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Speaker non trovato con l'ID fornito")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SpeakerResponseDto> getSpeakerById(@PathVariable Long id)
    {
        SpeakerResponseDto foundSpeaker = speakerService.getSpeakerById(id);
        return ResponseEntity.ok(foundSpeaker);
    }

    @Operation(
            summary = "Ottiene l'elenco di tutti gli speaker",
            description = "Recupera la lista completa di tutti i relatori registrati all'interno della piattaforma."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Elenco degli speaker restituito con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesto il ruolo ADMIN")
    })
    @GetMapping
    public ResponseEntity<List<SpeakerResponseDto>> getAllSpeakers()
    {
        List<SpeakerResponseDto> speakerList = speakerService.getAllSpeakers();

        return ResponseEntity.ok(speakerList);
    }

    @Operation(
            summary = "Aggiorna uno speaker esistente",
            description = "Modifica le informazioni anagrafiche, la biografia o le competenze di un " +
                    "relatore già presente a sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Speaker aggiornato con successo"),
            @ApiResponse(responseCode = "400", description = "Payload della richiesta non valido"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesto il ruolo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Speaker non trovato con l'ID fornito")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SpeakerResponseDto> updateSpeaker(@PathVariable Long id,
                                                            @Valid @RequestBody SpeakerRequestDto speakerRequestDto )
    {
        SpeakerResponseDto updatedSpeaker = speakerService.updateSpeaker(id, speakerRequestDto);
        return ResponseEntity.ok(updatedSpeaker);
    }

    @Operation(
            summary = "Elimina uno speaker dal sistema",
            description = "Rimuove permanentemente un relatore tramite il suo identificativo unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Speaker eliminato con successo (No Content)"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato"),
            @ApiResponse(responseCode = "403", description = "Accesso negato: richiesto il ruolo ADMIN"),
            @ApiResponse(responseCode = "404", description = "Speaker non trovato con l'ID fornito")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpeaker(@PathVariable Long id)
    {
        speakerService.deleteSpeaker(id);
        return ResponseEntity.noContent().build();
    }
}
