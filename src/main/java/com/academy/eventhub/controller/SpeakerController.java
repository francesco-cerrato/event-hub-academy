package com.academy.eventhub.controller;

import com.academy.eventhub.dto.SpeakerRequestDto;
import com.academy.eventhub.dto.SpeakerResponseDto;
import com.academy.eventhub.service.SpeakerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
/*
    La regola globale impostata in SecurityConfig consente l'accesso solo agli ADMIN
    .requestMatchers("/admin/**").hasRole("ADMIN")
 */
@RequestMapping("/admin/speakers")
public class SpeakerController
{
    private final SpeakerService speakerService;

    @Autowired
    public SpeakerController(SpeakerService speakerService)
    {
        this.speakerService = speakerService;
    }

    @PostMapping
    public ResponseEntity<SpeakerResponseDto> createSpeaker(@Valid @RequestBody SpeakerRequestDto speakerRequestDto)
    {
        SpeakerResponseDto createdSpeaker = speakerService.createSpeaker(speakerRequestDto);
        return new ResponseEntity<>(createdSpeaker, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpeakerResponseDto> getSpeakerById(@PathVariable Long id)
    {
        SpeakerResponseDto foundSpeaker = speakerService.getSpeakerById(id);
        return ResponseEntity.ok(foundSpeaker);
    }

    @GetMapping
    public ResponseEntity<List<SpeakerResponseDto>> getAllSpeakers()
    {
        List<SpeakerResponseDto> speakerList = speakerService.getAllSpeakers();

        return ResponseEntity.ok(speakerList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpeakerResponseDto> updateSpeaker(@PathVariable Long id,
                                                            @Valid @RequestBody SpeakerRequestDto speakerRequestDto )
    {
        SpeakerResponseDto updatedSpeaker = speakerService.updateSpeaker(id, speakerRequestDto);
        return ResponseEntity.ok(updatedSpeaker);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpeaker(@PathVariable Long id)
    {
        speakerService.deleteSpeaker(id);
        return ResponseEntity.noContent().build();
    }
}
