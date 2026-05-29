// Costante per l'endpoint corretto del ProfileController
const PROFILE_API_URL = 'http://localhost:8081/api/profiles/me';

/*
    Carica i dati del profilo dell'utente attualmente loggato (Lettura)
*/
async function loadUserProfile() {
    const authHeader = sessionStorage.getItem('authHeader');

    // Controllo di sicurezza preventivo sul client
    if (!authHeader) {
        alert("Accesso negato. Effettua il login.");
        window.location.href = 'login.html';
        return;
    }

    try {
        // Eseguiamo una GET verso l'endpoint /me del ProfileController
        const response = await fetch(PROFILE_API_URL, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Authorization': authHeader
            }
        });

        if (!response.ok) throw new Error("Impossibile recuperare i dati del profilo.");

        // Riceviamo il ProfileResponseDto dal backend
        const profileData = await response.json();

        // Popoliamo i campi del form HTML usando i dati reali ricevuti dal DB
        document.getElementById('profileUsername').value = sessionStorage.getItem('username') || '';
        document.getElementById('profileFirstname').value = profileData.firstName || '';
        document.getElementById('profileLastname').value = profileData.lastName || '';
        document.getElementById('profileCity').value = profileData.city || '';
        
        // Recupero ed esposizione di bio e avatarUrl
        document.getElementById('profileBio').value = profileData.bio || '';
        document.getElementById('profileAvatarUrl').value = profileData.avatarUrl || '';

    } catch (error) {
        console.error('Errore nel caricamento profilo:', error);
        alert("Errore durante il caricamento dei dati del profilo.");
    }
}

/*
    Invia i dati modificati al ProfileController (Scrittura tramite PUT)
*/
async function handleProfileSubmit(event) {
    event.preventDefault(); // Impedisce il reload nativo della pagina HTML

    const authHeader = sessionStorage.getItem('authHeader');

    // Costruiamo il payload leggendo dinamicamente i valori inseriti dall'utente
    // Mappato perfettamente sui vincoli del tuo ProfileUpdateDto
    const profilePayload = {
        firstName: document.getElementById('profileFirstname').value,
        lastName: document.getElementById('profileLastname').value,
        city: document.getElementById('profileCity').value,
        bio: document.getElementById('profileBio').value, // Ora invia la stringa digitata
        avatarUrl: document.getElementById('profileAvatarUrl').value // Ora invia l'URL inserito
    };

    try {
        // Eseguiamo la fetch PUT verso l'endpoint corretto del ProfileController
        const response = await fetch(PROFILE_API_URL, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: JSON.stringify(profilePayload)
        });

        if (response.ok) {
            alert("Profilo aggiornato con successo!");
            loadUserProfile(); // Ricarica la schermata per aggiornare i campi
        } else {
            // Se scattano i vincoli di Bean Validation (es. @Size bio > 255), estrae il messaggio d'errore (Step 3.8)
            const err = await response.json().catch(() => ({}));
            alert(`Errore: ${err.message || 'Impossibile aggiornare i dati anagrafici.'}`);
        }
    } catch (error) {
        console.error('Errore nella fetch di aggiornamento profilo:', error);
        alert("Connessione al server fallita.");
    }
}

// Registrazione degli eventi al caricamento completo del DOM nel browser
document.addEventListener('DOMContentLoaded', () => {
    loadUserProfile();

    const form = document.getElementById('profileForm');
    if (form) {
        form.addEventListener('submit', handleProfileSubmit);
    }
});
