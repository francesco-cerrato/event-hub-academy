package com.academy.eventhub.exception;


import java.time.LocalDateTime;

/*
    Modello standard per tutte le risposte di errore rilasciate dall'applicazione.
    Verrà serializzato in JSON e inviato al client.
*/
public class ErrorResponse
{
    private LocalDateTime timestamp;
    private int status; // Codice di stato HTTP (es. 404, 400)
    private String error; // Nome dell'errore HTTP (es. "Not Found")
    private String message; // Il messaggio di errore personalizzato
    private String path; // L'URL che ha generato l'errore (es. /api/users/99)

    public ErrorResponse()
    {}

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
