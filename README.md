# EventHub - Progetto Finale 2026

EventHub è l'applicazione creata per la fine del percorso in Academy. Serve a gestire e prenotare eventi come workshop o corsi, permettendo agli organizzatori di pubblicare le schede e agli utenti di prenotare i biglietti e lasciare recensioni.

---

## Schema del Database (Diagramma ER)

Ecco come ho strutturato le tabelle e le relazioni per il database del progetto:

![Diagramma ER](er-diagram.png)

---

## Cosa serve per farlo partire (Prerequisiti)

Prima di avviare il progetto, assicurati di avere sul computer:
- Java 17
- Docker Desktop attivo

---

## Come avviare il Database con Docker

Ho configurato Postgres e Adminer dentro un file docker-compose per non dover installare i programmi direttamente sul computer locale.

Per accendere il database a inizio giornata, apri il terminale nella cartella del progetto e lancia:
```bash
docker compose up -d
```

Quando hai finito e vuoi spegnere tutto liberando la RAM, usa invece:
```bash
docker compose down
```

### Dati per la connessione:
- **PostgreSQL:** gira su `localhost:5432` (Nome DB: `eventhub`, User: `eventhub_admin`, Password: `password123`)
- **Adminer (Interfaccia web):** basta andare nel browser a questo indirizzo: `http://localhost:8080` (quando fai il login, ricordati di scrivere `postgres-db` nel campo *Server*).
