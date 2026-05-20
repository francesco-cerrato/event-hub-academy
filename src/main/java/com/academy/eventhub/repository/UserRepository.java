package com.academy.eventhub.repository;

import com.academy.eventhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    // Utile per controllare se lo username, in fase di registrazione, è gia esistente
    boolean existsByUsername(String username);

    // Metodo fondamentale per recuperare l'utente loggato nel Service
    // SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);
}
