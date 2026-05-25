package com.academy.eventhub.service;

import com.academy.eventhub.dto.FeedbackRequestDto;
import com.academy.eventhub.dto.FeedbackResponseDto;
import com.academy.eventhub.entity.Feedback;
import com.academy.eventhub.entity.User;

import java.util.List;

public interface FeedbackService
{
    // CREATE: Creazione del feedback legato a un evento
    FeedbackResponseDto createFeedback(Long eventId, FeedbackRequestDto dto, String currentUsername);

    // READ: Dettaglio del singolo feedback
    FeedbackResponseDto getFeedbackById(Long feedbackId);

    // READ: Lista globale di tutti i feedback della piattaforma
    List<FeedbackResponseDto> getAllFeedbacks();

    // READ (Lista per evento): Tutti i feedback di un singolo evento
    List<FeedbackResponseDto> getFeedbacksByEvent(Long eventId);

    // UPDATE: Modifica di un feedback esistente
    FeedbackResponseDto updateFeedback(Long feedbackId, String currentUsername, FeedbackRequestDto dto);

    // DELETE: Eliminazione di un feedback
    void deleteFeedback(Long id, String currentUsername);

    // METRICA: Media dei voti di un evento (Punti 8 e 9 della traccia)
    Double getAverageRating(Long eventId);
}
