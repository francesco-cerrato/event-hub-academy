// Usiamo una variabile locale protetta per evitare conflitti con auth.js
const DETAIL_API_URL = 'http://localhost:8081/api/events';

let selectedEvent = null;

/*
    Recupera i dettagli del singolo evento
 */
async function fetchEventDetails() {
    const container = document.getElementById('eventDetailContainer');
    
    // Estrae il parametro 'id' dall'URL (?id=3)
    const urlParams = new URLSearchParams(window.location.search);
    const eventId = urlParams.get('id');

    if (!eventId) {
        container.innerHTML = '<p style="color: red;">Errore: Nessun ID specificato nell\'URL.</p>';
        return;
    }

    try {
        const response = await fetch(`${DETAIL_API_URL}/${eventId}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            container.innerHTML = '<p style="color: red;">Evento non trovato sul server.</p>';
            return;
        }

        selectedEvent = await response.json();
        renderEventDetail(selectedEvent);

    } catch (error) {
        console.error('Errore fetch dettagli:', error);
        container.innerHTML = '<p style="color: red;">Impossibile connettersi al server per caricare i dettagli.</p>';
    }
}

/*
    Mostra i dati dell'evento a schermo
*/
function renderEventDetail(event) {
    const container = document.getElementById('eventDetailContainer');
    
    const dateFormatted = event.eventDate ? new Date(event.eventDate).toLocaleString('it-IT') : 'Data non specificata';
    const tagsString = event.tags ? Array.from(event.tags).join(', ') : 'Nessuno';
    const speakersString = event.speakers ? Array.from(event.speakers).join(', ') : 'Nessuno';

    container.innerHTML = `
        <h1>${event.title}</h1>
        <hr>
        <p><strong>Descrizione:</strong> ${event.description}</p>
        <p><strong>Data:</strong> ${dateFormatted}</p>
        <p><strong>Sede:</strong> ${event.venue ? event.venue.name : 'Nessuna'}</p>
        <p><strong>Prezzo Standard:</strong> €${Number(event.price).toFixed(2)}</p>
        <p><strong>Prezzo VIP:</strong> €${Number(event.vipPrice).toFixed(2)}</p>
        <p><strong>Tag:</strong> <em>${tagsString}</em></p>
        <p><strong>Speaker:</strong> ${speakersString}</p>
    `;

    // Attiva il form di prenotazione (Punti 7, 8, 9)
    setupBookingForm(event);
}

/*
    PUNTI 7, 8, 9: Crea e mostra il Form di Prenotazione
*/
function setupBookingForm(event) {
    const bookingSection = document.getElementById('bookingSection');
    const isLoggedIn = sessionStorage.getItem('authHeader') !== null;

    // Punto 7: Consente la prenotazione solo se l'utente è loggato
    if (!isLoggedIn) {
        bookingSection.innerHTML = '<p>⚠️ Devi effettuare il <a href="login.html">Login</a> per poter prenotare i biglietti per questo evento.</p>';
        bookingSection.classList.remove('hidden');
        return;
    }

    // Punti 8 e 9: Iniezione del Form di prenotazione con scelta Ticket (STANDARD / VIP)
    bookingSection.innerHTML = `
        <h3>Invia la tua Prenotazione</h3>
        <br>
        <form id="ticketBookingForm">
            <label for="ticketType">Seleziona il tipo di biglietto:</label>
            <select id="ticketType" required style="padding: 5px; margin-left: 10px;">
                <option value="STANDARD">STANDARD (€${Number(event.price).toFixed(2)})</option>
                <option value="VIP">VIP (€${Number(event.vipPrice).toFixed(2)})</option>
            </select>
            <br><br>
            <button type="submit" style="padding: 5px 15px; cursor: pointer;">Conferma Prenotazione</button>
        </form>
    `;
    bookingSection.classList.remove('hidden');

    // Aggancia l'evento di invio del form alla funzione ufficiale (Punto 10)
    document.getElementById('ticketBookingForm').addEventListener('submit', handleTicketPurchase);
}

/*
    PUNTO 10: Logica di invio prenotazione al backend tramite fetch POST
 */
async function handleTicketPurchase(e) {
    e.preventDefault(); // Blocca il ricaricamento della pagina HTML

    // Recupera il tipo di biglietto selezionato nel form (STANDARD o VIP)
    const selectedType = document.getElementById('ticketType').value;

    // Recupera le credenziali Basic Auth salvate nel sessionStorage al momento del login
    const authHeader = sessionStorage.getItem('authHeader');

    if (!authHeader) {
        alert('Errore: Devi essere autenticato per effettuare una prenotazione.');
        window.location.href = 'login.html';
        return;
    }

    // Costruisce l'oggetto JSON mappato esattamente sul TicketRequestDto del backend
    const bookingPayload = {
        type: selectedType 
    };

    try {
        // Invia la richiesta POST all'endpoint /api/events/{id}/book
        const response = await fetch(`${DETAIL_API_URL}/${selectedEvent.id}/book`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'Authorization': authHeader // Passa le credenziali per popolare l'oggetto Principal
            },
            body: JSON.stringify(bookingPayload)
        });

        // Gestione della risposta del server (201 Created)
        if (response.status === 201) {
            const createdTicket = await response.json();
            alert(`🎉 Prenotazione completata con successo!\nID Biglietto: ${createdTicket.id}\nTipo Biglietto: ${selectedType}`);
            window.location.href = 'index.html'; // Sposta l'utente sulla Home o su una pagina a tua scelta
        } else {
            const errorData = await response.json().catch(() => ({}));
            alert(`⚠️ Impossibile prenotare: ${errorData.message || 'I posti per questo evento potrebbero essere esauriti.'}`);
        }

    } catch (error) {
        console.error('Errore durante la fetch di prenotazione:', error);
        alert('Impossibile connettersi al server per completare la prenotazione.');
    }
}

// Avvia l'ascolto in modo isolato e sicuro
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', fetchEventDetails);
} else {
    fetchEventDetails();
}
