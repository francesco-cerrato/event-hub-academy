const ADMIN_USERS_API = 'http://localhost:8081/admin/users';
const PUBLIC_USERS_API = 'http://localhost:8081/api/users';
const ADMIN_VENUES_API = 'http://localhost:8081/admin/venues';

/*
    Scarica tutti gli utenti del sistema e li elenca a schermo
*/
async function fetchAllUsers() {
    const container = document.getElementById('adminUsersContainer');
    const authHeader = sessionStorage.getItem('authHeader');

    try {
        const response = await fetch(PUBLIC_USERS_API, {
            method: 'GET',
            headers: {
                'Authorization': authHeader,
                'Accept': 'application/json'
            }
        });

        if (!response.ok) throw new Error(`Errore: ${response.status}`);

        const usersList = await response.json();
        renderUsersTable(usersList);

    } catch (error) {
        console.error('Errore caricamento utenti:', error);
        container.innerHTML = '<p style="color: red;">Impossibile caricare l\'elenco degli utenti.</p>';
    }
}

/*
    Renderizza la lista degli utenti con i relativi bottoni di azione
*/
function renderUsersTable(users) {
    const container = document.getElementById('adminUsersContainer');
    container.innerHTML = '';

    if (users.length === 0) {
        container.innerHTML = '<p>Nessun utente registrato nel sistema.</p>';
        return;
    }

    users.forEach(user => {
        const row = document.createElement('div');
        row.style.border = '1px solid #ccc';
        row.style.padding = '10px';
        row.style.marginBottom = '10px';

        const rolesString = user.roles ? Array.from(user.roles).join(', ') : 'Nessun ruolo';

        row.innerHTML = `
            <p><strong>ID:</strong> ${user.id} | <strong>Username:</strong> ${user.username}</p>
            <p><strong>Ruoli Attuali:</strong> <em>${rolesString}</em></p>
            <br>
            <button onclick="promoteToOrganizer(${user.id})" style="padding: 2px 10px; cursor: pointer;">Promuovi a Organizer</button>
            <button onclick="banUser(${user.id})" style="padding: 2px 10px; cursor: pointer; color: red; margin-left: 10px;">Banna/Elimina</button>
        `;
        container.appendChild(row);
    });
}

/*
    Promuove un utente al ruolo di ORGANIZER usando lo UserRoleUpdateDto esatto
*/
async function promoteToOrganizer(userId) {
    const authHeader = sessionStorage.getItem('authHeader');
    
    // Configurato millimetricamente sul UserRoleUpdateDto ("role": "ROLE_ORGANIZER")
    const payload = {
        role: "ROLE_ORGANIZER" 
    };

    if (!confirm("Sei sicuro di voler promuovere questo utente a Organizzatore?")) return;

    try {
        const response = await fetch(`${ADMIN_USERS_API}/${userId}/role`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            alert('Utente promosso con successo!');
            fetchAllUsers(); // Ricarica l'elenco utenti aggiornato
        } else {
            const err = await response.json().catch(() => ({}));
            alert(`Errore: ${err.message || 'Impossibile aggiornare il ruolo.'}`);
        }
    } catch (error) {
        console.error(error);
        alert('Connessione fallita.');
    }
}

/*
    Elimina (Banna) un utente dal sistema
*/
async function banUser(userId) {
    const authHeader = sessionStorage.getItem('authHeader');

    if (!confirm("⚠️ Sei sicuro di voler eliminare permanentemente questo utente dal sistema?")) return;

    try {
        const response = await fetch(`${PUBLIC_USERS_API}/${userId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': authHeader
            }
        });

        if (response.status === 204) {
            alert('Utente eliminato con successo dal sistema!');
            fetchAllUsers(); // Ricarica l'elenco utenti aggiornato
        } else {
            alert('Impossibile eliminare l\'utente. Verifica i permessi.');
        }
    } catch (error) {
        console.error(error);
        alert('Connessione fallita.');
    }
}

/* 
    Scarica tutte le sedi (Venue) e le elenca
*/
async function fetchAllVenues() {
    const container = document.getElementById('adminVenuesContainer');
    const authHeader = sessionStorage.getItem('authHeader');

    try {
        const response = await fetch(ADMIN_VENUES_API, {
            method: 'GET',
            headers: {
                'Authorization': authHeader,
                'Accept': 'application/json'
            }
        });

        if (!response.ok) throw new Error(`Errore: ${response.status}`);

        const venuesList = await response.json();
        renderVenuesList(venuesList);

    } catch (error) {
        console.error('Errore caricamento venue:', error);
        container.innerHTML = '<p style="color: red;">Impossibile caricare le sedi.</p>';
    }
}

function renderVenuesList(venues) {
    const container = document.getElementById('adminVenuesContainer');
    container.innerHTML = '';

    if (venues.length === 0) {
        container.innerHTML = '<p>Nessuna sede registrata a sistema.</p>';
        return;
    }

    venues.forEach(venue => {
        const item = document.createElement('div');
        item.style.border = '1px solid #555';
        item.style.padding = '8px';
        item.style.marginBottom = '8px';

        item.innerHTML = `
            <p><strong>ID Sede:</strong> ${venue.id} | <strong>Nome:</strong> ${venue.name}</p>
            <p><strong>Indirizzo:</strong> ${venue.address} | <strong>Capienza Massima:</strong> ${venue.capacity} posti</p>
        `;
        container.appendChild(item);
    });
}

/*
    Invia il form per registrare una nuova Venue
*/
async function handleVenueSubmit(event) {
    event.preventDefault();
    const authHeader = sessionStorage.getItem('authHeader');

    // Mappatura esatta sul tuo VenueRequestDto
    const venuePayload = {
        name: document.getElementById('venueName').value,
        address: document.getElementById('venueAddress').value,
        capacity: parseInt(document.getElementById('venueCapacity').value)
    };

    try {
        const response = await fetch(ADMIN_VENUES_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: JSON.stringify(venuePayload)
        });

        if (response.status === 201) {
            alert('Sede creata con successo!');
            document.getElementById('adminVenueForm').reset();
            fetchAllVenues(); // Ricarica la lista per includere la nuova riga
        } else {
            const err = await response.json().catch(() => ({}));
            alert(`Errore creazione sede: ${err.message || 'Controlla i dati immessi.'}`);
        }
    } catch (error) {
        console.error(error);
        alert('Connessione al server fallita.');
    }
}

// Inizializzazione della pagina
document.addEventListener('DOMContentLoaded', () => {
    fetchAllUsers();
    fetchAllVenues();

    const venueForm = document.getElementById('adminVenueForm');
    if (venueForm) venueForm.addEventListener('submit', handleVenueSubmit);
});
