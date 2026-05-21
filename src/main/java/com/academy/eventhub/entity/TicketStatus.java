package com.academy.eventhub.entity;


/*
    Rappresenta lo stato corrente del biglietto acquistato o prenotato dall'utente.

    Gli stati permettono di calcolare  correttamente i posti ancora disponibili
    per un evento, escludendo i biglietti che non sono più validi.
*/
public enum TicketStatus
{
    /*
        Il biglietto è valido e la prenotazione è confermata.
        I biglietti in questo stato occupano un posto e vengono conteggiati come "ticket attivi"
        nel calcolo dei posti disponibili dell'evento.
    */
    ACTIVE,

    /*
        La prenotazione è stata annullata dall'utente.
        Il biglietto viene invalidato a seguito dell'endpoint {@code DELETE /tickets/{id}}
        liberando il posto per la capienza della venue..
    */
    CANCELLED
}
