const ORGANIZER_API_URL = 'http://localhost:8081/api/events';

// Array locale per tracciare gli eventi scaricati e fare modifiche velocemente
let myEvents = [];

/*
    Recupera solo gli eventi creati da questo organizzatore
 */
async function fetchOrganizerEvents() {
    const container = document.getElementById('organizerEventsContainer');
    const authHeader = sessionStorage.getItem('authHeader');
    const currentUsername = sessionStorage.getItem('username');

    if (!authHeader || !currentUsername) {
        container.innerHTML = '<p style="color: red;">Accesso negato. Effettua il login.</p>';
        return;
    }

    try {
        // Usiamo il parametro query string 'organizer' supportato dall'EventController
        const response = await fetch(`${ORGANIZER_API_URL}?organizer=${currentUsername}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Authorization': authHeader
            }
        });

        if (!response.ok) throw new Error('Errore nel recupero dati.');

        const data = await response.json();
        // Se l'endpoint risponde con una pagina PageImpl, estrae .content, altrimenti usa l'array
        myEvents = data.content ? data.content : data;

        renderOrganizerEvents(myEvents);

    } catch (error) {
        console.error(error);
        container.innerHTML = '<p style="color: red;">Errore durante il caricamento degli eventi.</p>';
    }
}

/*
    Renderizza graficamente la lista degli eventi con i pulsanti di modifica
 */
function renderOrganizerEvents(list) {
    const container = document.getElementById('organizerEventsContainer');
    container.innerHTML = '';

    if (list.length === 0) {
        container.innerHTML = '<p>Non hai ancora pubblicato nessun evento.</p>';
        return;
    }

    list.forEach(event => {
        const item = document.createElement('div');
        item.style.border = '1px solid #000';
        item.style.padding = '10px';
        item.style.marginBottom = '10px';

        const dateFormatted = new Date(event.eventDate).toLocaleString('it-IT');

        item.innerHTML = `
            <h4>${event.title}</h4>
            <p><strong>Data:</strong> ${dateFormatted} | <strong>Sede ID:</strong> ${event.venue ? event.venue.id : 'Nessuna'}</p>
            <p><strong>Prezzi:</strong> Standard €${event.price.toFixed(2)} - VIP €${event.vipPrice.toFixed(2)}</p>
            <br>
            <button onclick="prepareEditEvent(${event.id})" style="padding: 2px 10px; cursor:pointer;">Modifica</button>
        `;
        container.appendChild(item);
    });
}

/*
    Gestore unico per l'invio del form (Creazione o Modifica)
*/
async function handleFormSubmit(event) {
    event.preventDefault();

    const authHeader = sessionStorage.getItem('authHeader');
    const id = document.getElementById('eventId').value; // Legge l'id nascosto
    
    // Costruzione del payload per il backend
    const eventPayload = {
        title: document.getElementById('eventTitle').value,
        description: document.getElementById('eventDescription').value,
        eventDate: document.getElementById('eventDate').value, // Formato datetime-local nativo
        price: parseFloat(document.getElementById('eventPrice').value),
        vipPrice: parseFloat(document.getElementById('eventVipPrice').value),
        venueId: parseInt(document.getElementById('eventVenue').value)
    };

    // Determina se è una creazione (POST) o una modifica (PUT)
    const isEdit = id !== '';
    const url = isEdit ? `${ORGANIZER_API_URL}/${id}` : ORGANIZER_API_URL;
    const method = isEdit ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: JSON.stringify(eventPayload)
        });

        if (response.ok) {
            alert(isEdit ? 'Evento modificato con successo!' : 'Evento creato con successo!');
            resetOrganizerForm();
            fetchOrganizerEvents(); // Ricarica la lista aggiornata
        } else {
            const err = await response.json().catch(() => ({}));
            alert(`Errore: ${err.message || 'Verifica i dati inseriti (es. validità dell\'ID sede).'}`);
        }
    } catch (error) {
        console.error(error);
        alert('Connessione al server fallita.');
    }
}

/*
    Carica i dati dell'evento selezionato all'interno del form per la modifica
*/
function prepareEditEvent(id) {
    const eventToEdit = myEvents.find(e => e.id === id);
    if (!eventToEdit) return;

    document.getElementById('formTitle').innerText = 'Modifica Evento esistente';
    document.getElementById('btnSubmitForm').innerText = 'Salva Modifiche';
    document.getElementById('btnCancelEdit').classList.remove('hidden');

    // Popola i campi del form
    document.getElementById('eventId').value = eventToEdit.id;
    document.getElementById('eventTitle').value = eventToEdit.title;
    document.getElementById('eventDescription').value = eventToEdit.description;
    
    // Taglia la stringa della data per renderla compatibile con l'input datetime-local (YYYY-MM-DDTHH:mm)
    if (eventToEdit.eventDate) {
        document.getElementById('eventDate').value = eventToEdit.eventDate.substring(0, 16);
    }
    
    document.getElementById('eventPrice').value = eventToEdit.price;
    document.getElementById('eventVipPrice').value = eventToEdit.vipPrice;
    document.getElementById('eventVenue').value = eventToEdit.venue ? eventToEdit.venue.id : '';
}

/*
    Ripristina il form allo stato originale di creazione
*/
function resetOrganizerForm() {
    document.getElementById('organizerEventForm').reset();
    document.getElementById('eventId').value = '';
    document.getElementById('formTitle').innerText = 'Crea un Nuovo Evento';
    document.getElementById('btnSubmitForm').innerText = 'Pubblica Evento';
    document.getElementById('btnCancelEdit').classList.add('hidden');
}

// Avvia il caricamento dei dati all'apertura della pagina
document.addEventListener('DOMContentLoaded', () => {
    fetchOrganizerEvents();
    
    const form = document.getElementById('organizerEventForm');
    if (form) form.addEventListener('submit', handleFormSubmit);
});
