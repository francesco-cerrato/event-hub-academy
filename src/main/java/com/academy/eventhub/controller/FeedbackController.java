package com.academy.eventhub.controller;

import com.academy.eventhub.dto.FeedbackRequestDto;
import com.academy.eventhub.dto.FeedbackResponseDto;
import com.academy.eventhub.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController
{
    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService)
    {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/events/{eventId}")
    public ResponseEntity<FeedbackResponseDto> createFeedback(@PathVariable Long eventId, @Valid @RequestBody FeedbackRequestDto feedbackRequestDto, Principal principal)
    {
        FeedbackResponseDto createdFeedback = feedbackService.createFeedback(eventId, feedbackRequestDto, principal.getName());
        return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FeedbackResponseDto>> getAllFeedbacks()
    {
        List<FeedbackResponseDto> feedbackList = feedbackService.getAllFeedbacks();

        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> getFeedbackById(@PathVariable Long id)
    {
        FeedbackResponseDto foundFeedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(foundFeedback);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksByEvents(@PathVariable Long eventId)
    {
        List<FeedbackResponseDto> feedbackList = feedbackService.getFeedbacksByEvent(eventId);
        return ResponseEntity.ok(feedbackList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> updateFeedback(@PathVariable Long id,
                                                              @Valid @RequestBody FeedbackRequestDto feedbackRequestDto,
                                                              Principal principal)
    {
        FeedbackResponseDto updatedFeedback = feedbackService.updateFeedback(id, principal.getName(), feedbackRequestDto);
        return ResponseEntity.ok(updatedFeedback);
    }

    @GetMapping("/events/{eventId}/rating")
    public ResponseEntity<Double> getEventRating(@PathVariable Long eventId)
    {
        Double average = feedbackService.getAverageRating(eventId);
        return ResponseEntity.ok(average);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id, Principal principal)
    {
        feedbackService.deleteFeedback(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
