package com.academy.eventhub.controller;

import com.academy.eventhub.dto.ProfileResponseDto;
import com.academy.eventhub.dto.ProfileUpdateDto;
import com.academy.eventhub.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/profiles")
@Tag(name = "Profiles", description = "Endpoint utente per la visualizzazione e la " +
        "gestione del proprio profilo personale.")
public class ProfileController
{
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService)
    {
        this.profileService = profileService;
    }

    /*
        Endpoint per visualizzare il proprio profilo
     */

    @Operation(
            summary = "Visualizza il proprio profilo",
            description = "Recupera le informazioni anagrafiche e di contatto (biografia, città, avatar) " +
                    "dell'utente attualmente autenticato nel sistema tramite il Principal."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dati del profilo recuperati con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato nel sistema"),
            @ApiResponse(responseCode = "404", description = "Profilo non trovato per l'utente loggato")
    })
    @GetMapping("/me")
    public ResponseEntity<ProfileResponseDto> getMyProfile(Principal principal)
    {
        String loggedUsername = principal.getName();

        ProfileResponseDto myProfile = profileService.getProfileByUsername(loggedUsername);

        return ResponseEntity.ok(myProfile);
    }

    @Operation(
            summary = "Aggiorna il proprio profilo",
            description = "Consente all'utente autenticato di modificare i propri dettagli personali " +
                    "come nome, cognome, biografia, città o URL dell'avatar."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profilo aggiornato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati di input non validi o non conformi"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato nel sistema"),
            @ApiResponse(responseCode = "404", description = "Profilo non trovato per l'utente loggato")
    })
    @PutMapping("/me")
    public ResponseEntity<ProfileResponseDto> updateMyProfile (Principal principal,
                                                               @Valid @RequestBody ProfileUpdateDto inputDto)
    {
        String loggedUsername = principal.getName();

        ProfileResponseDto updatedProfile = profileService.updateProfileByUsername(loggedUsername, inputDto);

        return ResponseEntity.ok(updatedProfile);
    }
}
