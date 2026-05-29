package com.academy.eventhub.specification;

import com.academy.eventhub.entity.Event;
import com.academy.eventhub.entity.Tag;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
    Classe helper contenente i criteri di filtraggio dinamico per l'entità Event.
    Sfrutta l'API Criteria di JPA per costruire query SQL in modo programmatico e sicuro.

    Ogni metodo restituisce una "Specification". Una Specification accetta tre parametri:
    root: rappresenta l'entità di partenza (in questo caso 'Event'), serve per selezionare i campi (es. root.get("title")).
    query: permette di modificare la struttura della query (es. aggiungere un ORDER BY o un DISTINCT).
    criteriaBuilder: usata per creare le condizioni logiche (equal, between, like, ecc.).
*/
public class EventSpecifications
{
    /*
        Filtra gli eventi che si svolgono in una data specifica
        (dalla mezzanotte al secondo prima della mezzanotte successiva).
    */
    public static Specification<Event> hasDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            // Se l'utente non ha selezionato alcuna data, non applica questo filtro (ritorna un pezzo di query vuoto)
            if (date == null) return null;

            // Trasformazione della data singola (es. 2026-10-15) in un intervallo di tempo completo per coprire l'intera giornata
            LocalDateTime startOfDay = date.atStartOfDay(); // 2026-10-15T00:00:00
            LocalDateTime endOfDay = date.atTime(23, 59, 59); // 2026-10-15T23:59:59

            // Genera l'equivalente SQL: WHERE event_date BETWEEN startOfDay AND endOfDay
            return criteriaBuilder.between(root.get("eventDate"), startOfDay, endOfDay);
        };


    }

    /*
        Filtra gli eventi in base all'ID della sede (Venue).
    */
    public static Specification<Event> hasVenueId(Long venueId) {
        return (root, query, criteriaBuilder) -> {
            // Se l'utente non ha selezionato alcuna sede, non applica questo filtro (ritorna un pezzo di query vuoto)
            if (venueId == null) return null;

            // Navighiamo dentro la relazione: partiamo da Event (root), entriamo nel campo "venue" e prendiamo il suo "id"
            // Genera l'equivalente SQL: WHERE venue_id = venueId
            return criteriaBuilder.equal(root.get("venue").get("id"), venueId);
        };
    }

    /*
        Filtra gli eventi in base allo username dell'organizzatore.
    */
    public static Specification<Event> hasOrganizerUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            // Controlla che la stringa non sia null o composta solo da spazi vuoti
            if (username == null || username.trim().isEmpty()) return null;

            // Navighiamo dentro la relazione: partiamo da Event (root), entriamo in "organizer" e verifichiamo lo "username"
            // Genera l'equivalente SQL: WHERE organizer.username = 'username_inviato'
            return criteriaBuilder.equal(root.get("organizer").get("username"), username);
        };
    }

    /*
        Filtra gli eventi in base al nome di un Tag (Gestisce la relazione Many-to-Many).
    */
    public static Specification<Event> hasTagName(String tagName) {
        return (root, query, criteriaBuilder) -> {
            if (tagName == null || tagName.trim().isEmpty()) return null;

            // Forza il DISTINCT per evitare la duplicazione degli eventi nella paginazione
            query.distinct(true);

            /*
                Poiché la relazione tra Event e Tag è Many-to-Many, a livello SQL è necessaria una INNER JOIN.
                Diciamo a JPA di fare un'operazione di JOIN partendo dall'entità corrente (root/Event)
                verso la collezione mappata dal campo "tags".
             */
            Join<Event, Tag> eventTagsJoin = root.join("tags");

            // Ora che siamo "dentro" la tabella dei tag unita, possiamo fare il controllo sul campo "name" di Tag
            // Genera l'equivalente SQL: INNER JOIN event_tags ON ... WHERE tag.name = 'nome_tag_inviato'
            return criteriaBuilder.equal(eventTagsJoin.get("name"), tagName);
        };
    }


}
