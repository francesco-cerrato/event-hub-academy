package com.academy.eventhub.exception;

/*
    Eccezione custom lanciata quando una risorsa (User, Profile, Event, ecc.)
    non viene trovata nel database.
*/
public class ResourceNotFoundException extends RuntimeException
{
    /*
        Costruttore che accetta il messaggio di errore specifico.
    */
    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}
