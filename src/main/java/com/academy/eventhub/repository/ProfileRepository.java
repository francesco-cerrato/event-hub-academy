package com.academy.eventhub.repository;

import com.academy.eventhub.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long>
{
    /*
        Sfrutta la potenza delle query automatiche di Spring Data JPA
        (chiamate Property Expressions). Poiché nella tua entità Profile
        il campo si chiama user (che fa riferimento all'entità User),
        e dentro l'entità User il campo si chiama username, Spring Data
        capisce da solo che deve fare una "JOIN" interna scrivendo
        il metodo in questo modo:
        findBy + User (l'oggetto dentro Profile) + Username (il campo dentro User).
     */
    Optional<Profile> findByUserUsername(String username);
}
