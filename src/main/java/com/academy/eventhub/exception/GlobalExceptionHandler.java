package com.academy.eventhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/*
    Punto 7 dello Step 3: Struttura globale per la cattura centralizzata delle eccezioni.
    Questa classe intercetta gli errori lanciati dai Controller e li trasforma in JSON ordinati.
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{

    /*
        Gestione di ResourceNotFoundException.
        Si attiva ogni volta che un utente, un profilo o un elemento non viene trovato.
        Restituisce un codice di stato HTTP 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException exception, WebRequest request)
    {
        // Costruzione DTO di errore con dati eccezione
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), // 404
                HttpStatus.NOT_FOUND.getReasonPhrase(), // "Not Found"
                exception.getMessage(), // Messaggio personalizzato del service
                request.getDescription(false) // Estrae il path dell'URL (es. uri=/api/users/99)
        );

        // Invio risposta formattata al client
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }






    /*
        Gestione della ValidationException.
        In Spring Boot, quando la validazione sui DTO (es. @NotBlank, @Size) fallisce,
        viene lanciata automaticamente l'eccezione MethodArgumentNotValidException.
        Restituisce un codice di stato HTTP 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception, WebRequest request) {

        // Raccola elenco errori di validazione (campo per campo)
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            // Mappa: nome del campo (es. "username") -> messaggio (es. "Lo username non può essere vuoto")
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // Trasformazione della mappa di errori in una stringa leggibile per il client
        String detailedMessage = "Campi non validi: " + errors.toString();

        // Costruzione DTO standard ErrorResponse
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),           // 400
                HttpStatus.BAD_REQUEST.getReasonPhrase(),      // "Bad Request"
                detailedMessage,                               // Elenco dettagliato dei campi errati
                request.getDescription(false)                  // L'URL che ha generato il fallimento
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /*
       Gestione delle violazioni delle regole di business (Punti 5 e 6 dello Step 8).
       Intercetta le IllegalStateException (es. posti esauriti, doppia prenotazione, evento passato).
       Restituisce un codice di stato HTTP 409 Conflict.
    */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException exception, WebRequest request)
    {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),                 // 409
                HttpStatus.CONFLICT.getReasonPhrase(),        // "Conflict"
                exception.getMessage(),                      // Messaggio dettagliato del Service
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /*
        Gestione degli argomenti non validi o tentativi di accesso non autorizzati a livello logico.
        Intercetta le IllegalArgumentException (es. utente che prova a cancellare un ticket non suo).
        Restituisce un codice di stato HTTP 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception, WebRequest request)
    {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),              // 400
                HttpStatus.BAD_REQUEST.getReasonPhrase(),     // "Bad Request"
                exception.getMessage(),                      // Messaggio di errore
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
