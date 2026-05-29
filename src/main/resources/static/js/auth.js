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
    GESTIONE DEL LOGIN (Basic Auth + Profilazione Ruoli)
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
        // Recuperiamo il profilo dell'utente dall'endpoint /me per estrarre i ruoli reali (Step 3.4)
        const response = await fetch(`${API_BASE_URL}/api/users/me`, {
            method: 'GET',
            headers: {
                'Authorization': authHeaderValue,
                'Accept': 'application/json'
            }
        });

        if (response.ok) {
            const userData = await response.json();
            
            // VERIFICA DEL BAN (REGOLA 9): Se l'utente ha solo il ruolo BANNED blocchiamo l'accesso
            if (userData.roles && userData.roles.includes('ROLE_BANNED')) {
                alert('Accesso negato: Il tuo account è stato sospeso dall\'amministratore.');
                sessionStorage.clear(); // Resetta preventivamente la sessione
                return;
            }

            // Se le credenziali sono giuste, salviamo l'header, lo username e l'array dei ruoli nella sessione del browser
            sessionStorage.setItem('authHeader', authHeaderValue);
            sessionStorage.setItem('username', username);
            sessionStorage.setItem('roles', JSON.stringify(userData.roles || []));
            
            alert('Login effettuato con successo!');
            window.location.href = 'index.html'; // Sposta l'utente sulla Home come utente loggato
        } else if (response.status === 401) {
            alert('Credenziali non corrette. Riprova.');
        } else if (response.status === 403) {
            alert('Accesso negato dal sistema di sicurezza.');
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
    let roles = [];
    
    // Tenta di estrarre l'elenco dei ruoli salvati in formato stringa JSON
    try {
        roles = JSON.parse(sessionStorage.getItem('roles')) || [];
    } catch(e) {
        roles = []; // Fallback in caso di sessione vuota
    }

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

        // PROFILAZIONE REALE DEI LINK GESTIONALI IN BASE AI RUOLI DEL DB (Step 11/12)
        if (navOrganizer) {
            if (roles.includes('ROLE_ORGANIZER') || roles.includes('ROLE_ADMIN')) {
                navOrganizer.classList.remove('hidden');
            } else {
                navOrganizer.classList.add('hidden');
            }
        }

        if (navAdmin) {
            if (roles.includes('ROLE_ADMIN')) {
                navAdmin.classList.remove('hidden');
            } else {
                navAdmin.classList.add('hidden');
            }
        }

    } else {
        // Se l'utente NON è loggato, mostriamo solo Login, Registrati ed Eventi
        if (navLogin) navLogin.classList.remove('hidden');
        if (navSignup) navSignup.classList.remove('hidden'); 

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
