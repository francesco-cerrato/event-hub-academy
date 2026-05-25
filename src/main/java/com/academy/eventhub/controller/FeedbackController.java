package com.academy.eventhub.controller;

import com.academy.eventhub.dto.FeedbackRequestDto;
import com.academy.eventhub.dto.FeedbackResponseDto;
import com.academy.eventhub.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@Tag(name = "Feedbacks", description = "Endpoint utente per la gestione delle recensioni e i punteggi di soddisfazione")
public class FeedbackController
{
    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService)
    {
        this.feedbackService = feedbackService;
    }

    @Operation(summary = "Lascia un feedback per un evento", description = "Consente l'inserimento di un rating (1-5) e commento solo se l'evento è concluso, l'utente ha un ticket valido e non ha già recensito l'evento.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Feedback registrato con successo"),
            @ApiResponse(responseCode = "400", description = "Violazione delle regole di business (evento non concluso, ticket mancante o recensione duplicata)")
    })
    @PostMapping("/events/{eventId}")
    public ResponseEntity<FeedbackResponseDto> createFeedback(@PathVariable Long eventId, @Valid @RequestBody FeedbackRequestDto feedbackRequestDto, Principal principal)
    {
        FeedbackResponseDto createdFeedback = feedbackService.createFeedback(eventId, feedbackRequestDto, principal.getName());
        return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
    }

    @Operation(summary = "Ottiene la lista globale dei feedback", description = "Recupera l'elenco completo di tutte le recensioni inserite nella piattaforma.")
    @ApiResponse(responseCode = "200", description = "Lista recuperata con successo")
    @GetMapping
    public ResponseEntity<List<FeedbackResponseDto>> getAllFeedbacks()
    {
        List<FeedbackResponseDto> feedbackList = feedbackService.getAllFeedbacks();

        return ResponseEntity.ok(feedbackList);
    }

    @Operation(summary = "Ottiene un singolo feedback tramite ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedback trovato"),
            @ApiResponse(responseCode = "404", description = "Feedback non trovato")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> getFeedbackById(@PathVariable Long id)
    {
        FeedbackResponseDto foundFeedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(foundFeedback);
    }

    @Operation(summary = "Ottiene i feedback di uno specifico evento")
    @ApiResponse(responseCode = "200", description = "Lista dei feedback dell'evento restituita con successo")
    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksByEvents(@PathVariable Long eventId)
    {
        List<FeedbackResponseDto> feedbackList = feedbackService.getFeedbacksByEvent(eventId);
        return ResponseEntity.ok(feedbackList);
    }

    @Operation(summary = "Modifica un feedback esistente", description = "Consente all'autore di aggiornare il voto e il commento del proprio feedback.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedback modificato con successo"),
            @ApiResponse(responseCode = "403", description = "Non sei l'autore di questo feedback"),
            @ApiResponse(responseCode = "404", description = "Feedback non trovato")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> updateFeedback(@PathVariable Long id,
                                                              @Valid @RequestBody FeedbackRequestDto feedbackRequestDto,
                                                              Principal principal)
    {
        FeedbackResponseDto updatedFeedback = feedbackService.updateFeedback(id, principal.getName(), feedbackRequestDto);
        return ResponseEntity.ok(updatedFeedback);
    }

    @Operation(summary = "Calcola la media dei voti di un evento", description = "Esegue l'aggregazione dei dati via JPQL per restituire il rating medio complessivo dell'evento.")
    @ApiResponse(responseCode = "200", description = "Media calcolata e restituita con successo")
    @GetMapping("/events/{eventId}/rating")
    public ResponseEntity<Double> getEventRating(@PathVariable Long eventId)
    {
        Double average = feedbackService.getAverageRating(eventId);
        return ResponseEntity.ok(average);
    }

    @Operation(summary = "Rimuove una recensione", description = "Consente all'autore di eliminare definitivamente il proprio feedback.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Feedback eliminato con successo"),
            @ApiResponse(responseCode = "403", description = "Non sei autorizzato a cancellare questo feedback")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id, Principal principal)
    {
        feedbackService.deleteFeedback(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
