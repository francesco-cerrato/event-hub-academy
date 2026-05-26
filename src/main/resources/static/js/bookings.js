const TICKETS_API_URL = 'http://localhost:8081/api/tickets/my-tickets';
const FEEDBACK_API_URL = 'http://localhost:8081/api/feedbacks/events';

/*
    Recupera i biglietti dell'utente autenticato
*/
async function fetchMyBookings() {
    const container = document.getElementById('bookingsContainer');
    const authHeader = sessionStorage.getItem('authHeader');

    if (!authHeader) {
        container.innerHTML = '<p style="color: red;">Accesso negato. Effettua il <a href="login.html">Login</a>.</p>';
        return;
    }

    try {
        const response = await fetch(TICKETS_API_URL, {
            method: 'GET',
            headers: {
                'Authorization': authHeader,
                'Accept': 'application/json'
            }
        });

        if (!response.ok) throw new Error(`Errore: ${response.status}`);

        const ticketsList = await response.json();
        renderBookings(ticketsList);

    } catch (error) {
        console.error('Errore recupero prenotazioni:', error);
        container.innerHTML = '<p style="color: red;">Impossibile caricare le tue prenotazioni.</p>';
    }
}

/*
    Mostra l'elenco dei biglietti leggendo le proprietà piatte del tuo TicketResponseDto (Punto 11 e 12)
*/
function renderBookings(tickets) {
    const container = document.getElementById('bookingsContainer');
    container.innerHTML = '';

    if (tickets.length === 0) {
        container.innerHTML = '<p>Non hai ancora effettuato nessuna prenotazione.</p>';
        return;
    }

    const now = new Date();

    tickets.forEach(ticket => {
        const box = document.createElement('div');
        box.style.border = '1px solid #ccc';
        box.style.padding = '15px';
        box.style.marginBottom = '15px';

        // Mappatura sulle proprietà piatte del DTO
        const ticketId = ticket.id;
        const eventTitle = ticket.eventTitle || 'Evento Sconosciuto';
        const eventId = ticket.event_id; // Legge dal tuo metodo getEvent_id()
        const eventDateStr = ticket.eventDate;
        const ticketType = ticket.type || 'STANDARD';
        const pricePaid = ticket.pricePaid ? Number(ticket.pricePaid).toFixed(2) : '0.00';

        const dateFormatted = eventDateStr ? new Date(eventDateStr).toLocaleString('it-IT') : 'Data non disponibile';

        // Struttura base del biglietto stampata a schermo
        let htmlContent = `
            <h3>Biglietto #${ticketId} — ${eventTitle}</h3>
            <p><strong>Data Evento:</strong> ${dateFormatted}</p>
            <p><strong>Tipologia Biglietto:</strong> ${ticketType} (Pagato: €${pricePaid})</p>
            <p><strong>Stato Biglietto:</strong> ${ticket.status || 'ACTIVE'}</p>
            <br>
        `;

        // Controllo se l'evento è già concluso rispetto al tempo reale corrente (oggi è 26 Maggio 2026)
        if (eventDateStr && new Date(eventDateStr) < now) {
            htmlContent += `
                <div style="background: #f9f9f9; padding: 10px; border-top: 1px dashed #000; margin-top: 10px;">
                    <h5>Lascia un Feedback per questo evento concluso</h5>
                    <br>
                    <form onsubmit="submitFeedback(event, ${eventId})">
                        <label>Voto (1-5):</label>
                        <input type="number" id="rating-${eventId}" min="1" max="5" required style="width: 50px; margin-left: 5px;">
                        
                        <label style="margin-left: 15px;">Commento:</label>
                        <input type="text" id="comment-${eventId}" placeholder="Il tuo commento qui..." required style="width: 250px; margin-left: 5px;">
                        
                        <button type="submit" style="margin-left: 15px; padding: 2px 10px; cursor: pointer;">Invia Feedback</button>
                    </form>
                </div>
            `;
        } else {
            htmlContent += `<p style="color: green; font-size: 0.9rem;">⏳ L'evento non è ancora iniziato o concluso. Potrai lasciare un feedback al suo termine.</p>`;
        }

        box.innerHTML = htmlContent;
        container.appendChild(box);
    });
}

/*
    Invia il feedback al FeedbackController tramite fetch POST
*/
async function submitFeedback(event, eventId) {
    event.preventDefault(); // Impedisce il ricaricamento della pagina HTML
    const authHeader = sessionStorage.getItem('authHeader');

    const ratingValue = parseInt(document.getElementById(`rating-${eventId}`).value);
    const commentValue = document.getElementById(`comment-${eventId}`).value.trim();

    // Mappatura sulle proprietà del FeedbackRequestDto
    const feedbackPayload = {
        rating: ratingValue,
        comment: commentValue
    };

    try {
        const response = await fetch(`${FEEDBACK_API_URL}/${eventId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: JSON.stringify(feedbackPayload)
        });

        if (response.status === 201) {
            alert('🎉 Feedback registrato con successo! Grazie per la tua recensione.');
            fetchMyBookings(); // Ricarica la lista aggiornata
        } else {
            const err = await response.json().catch(() => ({}));
            alert(`⚠️ Impossibile inserire il feedback: ${err.message || 'Hai già recensito questo evento o non possiedi un biglietto valido.'}`);
        }
    } catch (error) {
        console.error('Errore invio feedback:', error);
        alert('Connessione al server fallita.');
    }
}

// Avvia il caricamento automatico delle prenotazioni all'apertura del file HTML
document.addEventListener('DOMContentLoaded', fetchMyBookings);
