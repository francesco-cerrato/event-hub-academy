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
        FeedbackResponseDto feedbackResponseDto = feedbackService.createFeedback(eventId, feedbackRequestDto, principal.getName());
        return new ResponseEntity<>(feedbackResponseDto, HttpStatus.CREATED);
    }
}
