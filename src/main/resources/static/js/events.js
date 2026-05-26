const EVENTS_API_URL = 'http://localhost:8081/api/events';

// Array globale per memorizzare gli eventi scaricati dal server
let allEvents = [];

/*
    Scarica gli eventi dal backend e li mostra a schermo
*/
async function fetchAndDisplayEvents() {
    const container = document.getElementById('eventsContainer');
    
    try {
        const response = await fetch(EVENTS_API_URL, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Errore server: ${response.status}`);
        }

        // Scarica la risposta JSON dal server
        const data = await response.json();
        
        /* 
           CORREZIONE FONDAMENTALE:
           Se il backend restituisce una Page di Spring Data, gli eventi reali sono dentro data.content.
           Se restituisce una lista standard, usa direttamente data.
        */
        allEvents = data.content ? data.content : data;
        
        // Renderizza la lista completa degli eventi
        renderEvents(allEvents);

    } catch (error) {
        console.error('Errore nel recupero degli eventi:', error);
        container.innerHTML = '<p style="color: red;">Impossibile caricare gli eventi al momento.</p>';
    }
}

/*
    Funzione di utilità per generare i blocchi HTML di ogni evento
*/
function renderEvents(eventsList) {
    const container = document.getElementById('eventsContainer');
    container.innerHTML = ''; // Svuota il contenitore

    if (!eventsList || eventsList.length === 0) {
        container.innerHTML = '<p>Nessun evento disponibile con i filtri selezionati.</p>';
        return;
    }

    // Cicla gli eventi e costruisce la struttura HTML essenziale per ciascuno
    eventsList.forEach(event => {
        const eventBox = document.createElement('div');
        eventBox.style.border = '1px solid #ccc';
        eventBox.style.padding = '15px';
        eventBox.style.marginBottom = '15px';

        // Formatta la data (YYYY-MM-DDTHH:mm:ss -> Leggibile in italiano)
        const dateFormatted = new Date(event.eventDate).toLocaleString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

        // Estrae i tag unendoli con una virgola
        const tagsString = event.tags && event.tags.size ? Array.from(event.tags).join(', ') : (event.tags && event.tags.length > 0 ? event.tags.join(', ') : 'Nessuno');

        // Struttura minimale con titolo, data, prezzo standard e tag
        eventBox.innerHTML = `
            <h2>${event.title}</h2>
            <p><strong>Data:</strong> ${dateFormatted}</p>
            <p><strong>Prezzo Base:</strong> €${event.price.toFixed(2)}</p>
            <p><strong>Tag:</strong> <em>${tagsString}</em></p>
            <br>
            <!-- Bottone richiesto per i punti successivi (Dettaglio) -->
            <button onclick="viewDetails(${event.id})" style="padding: 5px 10px; cursor: pointer;">Visualizza Dettaglio</button>
        `;

        container.appendChild(eventBox);
    });
}

/*
    Applica i filtri impostati dall'utente
*/
function applyFilters() {
    const filterDateValue = document.getElementById('filterDate').value; // Formato dell'input: YYYY-MM-DD
    const filterTagValue = document.getElementById('filterTag').value.trim().toLowerCase();

    // Filtra l'array globale degli eventi senza rifare la chiamata al server
    const filtered = allEvents.filter(event => {
        let matchDate = true;
        let matchTag = true;

        // Filtro Data (Punto 3)
        if (filterDateValue) {
            // Estrae solo la parte iniziale YYYY-MM-DD dalla stringa data dell'evento (es: 2026-05-26T12:51...)
            const eventDateStr = event.eventDate.substring(0, 10);
            matchDate = (eventDateStr === filterDateValue);
        }

        // Filtro Tag (Punto 4)
        if (filterTagValue) {
            // Controlla se almeno uno dei tag dell'evento contiene la stringa cercata
            matchTag = event.tags && Array.from(event.tags).some(tag => 
                tag.toLowerCase().includes(filterTagValue)
            );
        }

        return matchDate && matchTag;
    });

    renderEvents(filtered);
}

/*
    Ripristina i campi di filtro e mostra la lista totale
*/
function resetFilters() {
    document.getElementById('filterDate').value = '';
    document.getElementById('filterTag').value = '';
    renderEvents(allEvents);
}

/*
    Funzione di reindirizzamento al dettaglio (Punto 5 dello Step 12)
*/
function viewDetails(eventId) {
    // Salva l'id dell'evento cliccato per poterlo leggere nella pagina successiva
    window.location.href = `event-detail.html?id=${eventId}`;
}

/*
    Avvio automatico al caricamento della pagina
*/
document.addEventListener('DOMContentLoaded', () => {
    // Scarica e mostra gli eventi
    fetchAndDisplayEvents();

    // Aggancia gli eventi ai bottoni dei filtri della pagina HTML
    const btnApply = document.getElementById('btnApplyFilters');
    if (btnApply) btnApply.addEventListener('click', applyFilters);

    const btnReset = document.getElementById('btnResetFilters');
    if (btnReset) btnReset.addEventListener('click', resetFilters);
});
