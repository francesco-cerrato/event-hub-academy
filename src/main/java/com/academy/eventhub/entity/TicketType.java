package com.academy.eventhub.entity;



/*
    Rappresenta la tipologia di biglietto selezionata dall'utente in fase di prenotazione.

    Questa enumerazione viene utilizzata all'interno dell'entità join {@code Ticket}
    per definire le regole di business della prenotazione, influenzando
    il calcolo del prezzo finale del biglietto e i controlli sulla disponibilità
    dei posti nell'evento.
 */
public enum TicketType
{
    /*
        Biglietto a tariffa e accesso ordinario.
        Selezionato dall'utente per una prenotazione standard.
    */
    STANDARD,

/*
    Biglietto a tariffa premium con privilegi speciali.
    Selezionato dall'utente per una prenotazione esclusiva.
*/
    VIP
}
