package com.academy.eventhub.dto;

import com.academy.eventhub.entity.TicketType;
import com.academy.eventhub.entity.User;
import jakarta.validation.constraints.NotNull;

/*
    In questo caso, il TicketRequestDto contiene solamente il type in quanto
    i restanti dati necessari a creare il ticket sono già posseduti dall'applicazione
    (Controller EventController)

    L'id è generato automaticamente, l'evento (event_id) è ricavabile direttamente
    dall'URL dell'endpoint ("/events/{id}/book"), l'utente (user_id) è recuperato
    automaticamente dal contesto di sicurezza (Principal), lo status è sempre "ACTIVE"
    all'atto della prenotazione di un nuovo ticket, la data di creazione (createdAt) è impostata
    automaticamente all'atto della creazione e il prezzo pagato (pricePaid) è selezionato
    in base al biglietto scelto
 */
public class TicketRequestDto
{
    @NotNull(message = "Il tipo di biglietto è obbligatorio (STANDARD o VIP)")
    private TicketType type;

    public TicketRequestDto()
    {

    }

    public TicketRequestDto(TicketType type) {
        this.type = type;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }
}
