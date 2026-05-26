const API_BASE_URL = 'http://localhost:8081'; 


/*
    GESTIONE DELLA REGISTRAZIONE (Sign up)
*/
async function handleSignup(event) {
    event.preventDefault(); // Blocca il ricaricamento della pagina HTML

    // Recupera i valori inseriti dall'utente nei campi dell'HTML
    const username = document.getElementById('signupUsername').value;
    const password = document.getElementById('signupPassword').value;
    const firstName = document.getElementById('signupFirstname').value;
    const lastName = document.getElementById('signupLastname').value;
    const city = document.getElementById('signupCity').value;

    // Costruisce l'oggetto JSON mappato esattamente sul RegisterRequest DTO del backend
    const signupData = {
        username: username,
        password: password,
        firstName: firstName,
        lastName: lastName,
        city: city
    };

    try {
        const response = await fetch(`${API_BASE_URL}/auth/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(signupData)
        });

        if (response.ok) {
            alert('Registrazione avvenuta con successo! Adesso puoi accedere.');
            window.location.href = 'login.html'; // Sposta l'utente alla pagina di login
        } else {
            const errorData = await response.json().catch(() => ({}));
            alert(`Errore durante la registrazione: ${errorData.message || 'Controlla i dati inseriti.'}`);
        }
    } catch (error) {
        console.error('Errore nella fetch di registrazione:', error);
        alert('Impossibile connettersi al server backend.');
    }
}

/*
    GESTIONE DEL LOGIN (Basic Auth)
*/
async function handleLogin(event) {
    event.preventDefault(); // Blocca il ricaricamento della pagina HTML

    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    // Codifica le credenziali in formato Base64 per lo standard HTTP Basic Auth
    const credentials = btoa(`${username}:${password}`);
    const authHeaderValue = `Basic ${credentials}`;

    try {
        // Interroghiamo una rotta protetta del backend per testare se le credenziali sono valide.
        // Se non hai ancora una rotta specifica, usiamo la lista eventi che richiede l'autenticazione.
        const response = await fetch(`${API_BASE_URL}/api/events`, {
            method: 'GET',
            headers: {
                'Authorization': authHeaderValue
            }
        });

        if (response.ok) {
            // Se le credenziali sono giuste, salviamo l'header e lo username nella sessione del browser
            sessionStorage.setItem('authHeader', authHeaderValue);
            sessionStorage.setItem('username', username);
            
            alert('Login effettuato con successo!');
            window.location.href = 'index.html'; // Sposta l'utente sulla Home come utente loggato
        } else if (response.status === 401) {
            alert('Credenziali non corrette. Riprova.');
        } else {
            alert('Errore di autenticazione da parte del server.');
        }
    } catch (error) {
        console.error('Errore nella fetch di login:', error);
        alert('Impossibile connettersi al server backend.');
    }
}

/*
    GESTIONE DEL LOGOUT
*/
function logout() {
    sessionStorage.clear(); // Cancella tutti i dati di sessione salvati (token, ruoli, username)
    alert('Disconnessione effettuata.');
    window.location.href = 'index.html'; // Riporta l'utente alla home pubblica
}

/*
    AGGANCIO DINAMICO DELLA NAVBAR IN BASE ALLA SESSIONE
*/
function updateNavbar() {
    const isLoggedIn = sessionStorage.getItem('authHeader') !== null;

    // Recuperiamo tutti gli elementi della navbar tramite il loro ID
    const navLogin = document.getElementById('navLogin');
    const navSignup = document.getElementById('navSignup');
    const navBookings = document.getElementById('navBookings');
    const navProfile = document.getElementById('navProfile');
    const navOrganizer = document.getElementById('navOrganizer');
    const navAdmin = document.getElementById('navAdmin');
    const navLogout = document.getElementById('navLogout');

    if (isLoggedIn) {
        // Se l'utente è loggato, nascondiamo Login e Registrati
        if (navLogin) navLogin.classList.add('hidden');
        if (navSignup) navSignup.classList.add('hidden');

        // Mostriamo i link dell'utente autenticato
        if (navBookings) navBookings.classList.remove('hidden');
        if (navProfile) navProfile.classList.remove('hidden');
        if (navLogout) navLogout.classList.remove('hidden');

        // Per ora mostriamo tutti i pannelli gestionali (Verranno profilati nello Step 12)
        if (navOrganizer) navOrganizer.classList.remove('hidden');
        if (navAdmin) navAdmin.classList.remove('hidden');

    } else {
        // Se l'utente NON è loggato, mostriamo solo Login, Registrati ed Eventi
        if (navLogin) navLogin.classList.remove('hidden');
        if (navSignup) navSignup.classList.remove('hidden'); // Corretto qui (rimosso .github)

        // Nascondiamo tutto il resto
        if (navBookings) navBookings.classList.add('hidden');
        if (navProfile) navProfile.classList.add('hidden');
        if (navOrganizer) navOrganizer.classList.add('hidden');
        if (navAdmin) navAdmin.classList.add('hidden');
        if (navLogout) navLogout.classList.add('hidden');
    }
}

/*
    AGGANCIO DEGLI EVENTI AI FORM HTML
    Questo blocco si attiva automaticamente non appena la pagina viene caricata nel browser
*/
document.addEventListener('DOMContentLoaded', () => {
    // Aggiorna lo stato visivo della navbar in base alla sessione corrente
    updateNavbar();

    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});
