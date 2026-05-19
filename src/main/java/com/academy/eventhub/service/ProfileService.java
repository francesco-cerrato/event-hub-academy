package com.academy.eventhub.service;

import com.academy.eventhub.dto.ProfileResponseDto;
import com.academy.eventhub.dto.ProfileUpdateDto;

import java.util.List;

/*
    Interfaccia che definisce le operazioni CRUD per la gestione dei profili utenti.
 */
public interface ProfileService
{
    // READ - Recupero profili (singolo o tutti)
    public List<ProfileResponseDto> getAllProfiles();
    public ProfileResponseDto getProfileById(Long id);

    // UPDATE - Aggiornamento dati anagrifici di un profilo dato il suo id
    public ProfileResponseDto updateProfile(Long id, ProfileUpdateDto inputDto);

    /*
        Non è necessario implementare CREATE e DELETE in quanto
        creazione e cancellazione avvengono in cascata (CascadeType.ALL)
        alla creazione/cancellazione dello User.
     */

    ProfileResponseDto getProfileByUsername(String username);
    ProfileResponseDto updateProfileByUsername(String username, ProfileUpdateDto inputDto);
}
