package com.academy.eventhub.service;

import com.academy.eventhub.dto.FeedbackRequestDto;
import com.academy.eventhub.dto.FeedbackResponseDto;
import com.academy.eventhub.dto.TicketResponseDto;
import com.academy.eventhub.dto.VenueResponseDto;
import com.academy.eventhub.entity.*;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.EventRepository;
import com.academy.eventhub.repository.FeedbackRepository;
import com.academy.eventhub.repository.TicketRepository;
import com.academy.eventhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService
{

    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, EventRepository eventRepository,
                               UserRepository userRepository, TicketRepository ticketRepository)
    {
        this.feedbackRepository = feedbackRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }


    @Override
    @Transactional
    public FeedbackResponseDto createFeedback(Long eventId, FeedbackRequestDto dto, String currentUsername)
    {
        // Recupero dell'utente loggato dal database tramite l'username fornito dal Principal
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow( () -> new ResourceNotFoundException("Utente non trovato con username: " + currentUsername));

        // Verifica dell'esistenza dell'evento
        Event foundEvent = eventRepository.findById(eventId)
                .orElseThrow( () -> new ResourceNotFoundException("Evento non trovato con id: " + eventId));

        // Consentire il feedback solo se l'evento è concluso
        // Se la data dell'evento si trova nel futuro rispetto al momento attuale, l'operazione si blocca
        if (foundEvent.getEventDate().isAfter(LocalDateTime.now()))
        {
            throw new IllegalStateException("Non è possibile lasciare un feedback per un evento non ancora concluso");
        }

        // Consentire il feedback solo se l'utente possiede un ticket valido (stato ACTIVE)
        boolean hasValidTicket = ticketRepository.existsByUserIdAndEventIdAndStatus(currentUser.getId(), eventId, TicketStatus.ACTIVE);
        if (!hasValidTicket)
        {
            throw new IllegalStateException("Operazione negata: non possiedi un biglietto attivo per questo evento");
        }

        // Impedire recensioni duplicate per lo stesso evento dallo stesso utente
        if (feedbackRepository.existsByUserIdAndEventId(currentUser.getId(), eventId)) {
            throw new IllegalStateException("Hai già lasciato un feedback per questo evento.");
        }

        // Costruzione dell'entità Feedback e popolamento dei dati
        Feedback newFeedback = new Feedback();
        newFeedback.setRating(dto.getRating());
        newFeedback.setComment(dto.getComment());
        newFeedback.setUser(currentUser);
        newFeedback.setEvent(foundEvent);

        // Persistenza dell'oggetto sul database
        Feedback savedFeedback = feedbackRepository.save(newFeedback);

        // Conversione finale nel DTO di risposta utilizzando il tuo metodo helper
        return convertToResponseDto(savedFeedback);
    }

    @Override
    public FeedbackResponseDto getFeedbackById(Long feedbackId)
    {
        Feedback foundFeedback = feedbackRepository.findById(feedbackId)
                .orElseThrow( () -> new ResourceNotFoundException("Feedback non trovato con id: " + feedbackId));
        return convertToResponseDto(foundFeedback);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getAllFeedbacks()
    {
        List<Feedback> feedbackList = feedbackRepository.findAll();

        List<FeedbackResponseDto> dtoList = new ArrayList<>();

        for (Feedback feedback : feedbackList)
        {
            FeedbackResponseDto dto = convertToResponseDto(feedback);

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getFeedbacksByEvent(Long eventId)
    {
        if (!eventRepository.existsById(eventId))
        {
            throw new ResourceNotFoundException("Evento non trovato con id: " + eventId);
        }

        List<Feedback> feedbackList = feedbackRepository.findByEventId(eventId);

        List<FeedbackResponseDto> dtoList = new ArrayList<>();

        for (Feedback feedback : feedbackList)
        {
            FeedbackResponseDto dto = convertToResponseDto(feedback);

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public FeedbackResponseDto updateFeedback(Long feedbackId, String currentUsername, FeedbackRequestDto dto)
    {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow( () -> new ResourceNotFoundException("Utente non trovato con username: " + currentUsername));

        Feedback foundFeedback = feedbackRepository.findById(feedbackId)
                .orElseThrow( () -> new ResourceNotFoundException("Feedback non trovato con id: " + feedbackId) );

        if (!foundFeedback.getUser().getId().equals(currentUser.getId()))
        {
            throw new IllegalStateException("Operazione negata: non sei autorizzato a modificare questo feedback");
        }


        foundFeedback.setRating(dto.getRating());
        foundFeedback.setComment(dto.getComment());

        Feedback updatedFeedback = feedbackRepository.save(foundFeedback);

        return convertToResponseDto(updatedFeedback);
    }

    @Override
    @Transactional
    public void deleteFeedback(Long id, String currentUsername)
    {

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow( () -> new ResourceNotFoundException("Utente non trovato con username: " + currentUsername));

        Feedback foundFeedback = feedbackRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Feedback non trovato con id: " + id) );


        // Verifica che l'ID dell'utente loggato coincida con l'ID di chi ha scritto il feedback
        if (!foundFeedback.getUser().getId().equals(currentUser.getId()))
        {
            throw new IllegalStateException("Operazione negata: non sei autorizzato a cancellare questo feedback");
        }

        feedbackRepository.delete(foundFeedback);
    }

    @Override
    public Double getAverageRating(Long eventId)
    {
        // // Verifica dell'esistenza dell'evento
        if (!eventRepository.existsById(eventId))
        {
            throw new ResourceNotFoundException("Evento non trovato con id: " + eventId);
        }

        // Recupera la media calcolata tramite la query JPQL AVG del repository
        Double average = feedbackRepository.getAverageRatingByEventId(eventId);

        // Se l'evento non ha recensioni il DB restituisce null; in tal caso si restituisce 0.0
        return average != null ? average : 0.0;
    }

    private FeedbackResponseDto convertToResponseDto(Feedback feedback)
    {
        FeedbackResponseDto feedbackResponseDto = new FeedbackResponseDto();

        feedbackResponseDto.setId(feedback.getId());
        feedbackResponseDto.setRating(feedback.getRating());
        feedbackResponseDto.setComment(feedback.getComment());
        feedbackResponseDto.setUsername(feedback.getUser().getUsername());

        return feedbackResponseDto;
    }
}
