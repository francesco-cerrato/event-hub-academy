package com.academy.eventhub.service;

import com.academy.eventhub.dto.SpeakerRequestDto;
import com.academy.eventhub.dto.SpeakerResponseDto;

import java.util.List;

public interface SpeakerService
{
    SpeakerResponseDto createSpeaker(SpeakerRequestDto speakerRequestDto);
    SpeakerResponseDto getSpeakerById(Long id);
    List<SpeakerResponseDto> getAllSpeakers();
    SpeakerResponseDto updateSpeaker(Long id, SpeakerRequestDto dto);
    void deleteSpeaker(Long id);
}
