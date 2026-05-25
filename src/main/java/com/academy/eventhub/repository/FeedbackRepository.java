package com.academy.eventhub.repository;

import com.academy.eventhub.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>
{
    // Recupera tutti i feedback legati a uno specifico evento
    List<Feedback> findByEventId(Long eventId);

    // Punto 7: Verifica se un utente ha già lasciato un feedback per un determinato evento
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    // Punti 8 e 9: Query custom per calcolare la media aritmetica dei voti di un evento
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.event.id = :eventId")
    Double getAverageRatingByEventId(@Param("eventId") Long eventId);
}
