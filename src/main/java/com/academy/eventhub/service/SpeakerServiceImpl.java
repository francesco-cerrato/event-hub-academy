package com.academy.eventhub.service;

import com.academy.eventhub.dto.SpeakerRequestDto;
import com.academy.eventhub.dto.SpeakerResponseDto;
import com.academy.eventhub.entity.Speaker;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.SpeakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpeakerServiceImpl implements SpeakerService
{

    private final SpeakerRepository speakerRepository;

    @Autowired
    public SpeakerServiceImpl(SpeakerRepository speakerRepository)
    {
        this.speakerRepository = speakerRepository;
    }

    @Override
    @Transactional
    public SpeakerResponseDto createSpeaker(SpeakerRequestDto speakerRequestDto)
    {
        Speaker newSpeaker = new Speaker();
        newSpeaker.setName(speakerRequestDto.getName());
        newSpeaker.setBio(speakerRequestDto.getBio());

        Speaker savedSpeaker = speakerRepository.save(newSpeaker);

        return convertToResponseDto(savedSpeaker);
    }

    @Override
    @Transactional(readOnly = true)
    public SpeakerResponseDto getSpeakerById(Long id)
    {

        Speaker foundSpeaker = speakerRepository.findById(id)
                .orElseThrow( () ->  new ResourceNotFoundException("Speaker non trovato con id: " + id));

        return convertToResponseDto(foundSpeaker);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpeakerResponseDto> getAllSpeakers()
    {
        List<Speaker> speakerList = speakerRepository.findAll();
        List<SpeakerResponseDto> dtoList = new ArrayList<>();

        for (Speaker speaker : speakerList) {
            dtoList.add(convertToResponseDto(speaker));
        }
        return dtoList;
    }

    @Override
    @Transactional
    public SpeakerResponseDto updateSpeaker(Long id, SpeakerRequestDto dto)
    {
        Speaker foundSpeaker = speakerRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Speaker non trovato con id: " + id));

        foundSpeaker.setName(dto.getName());
        foundSpeaker.setBio(dto.getBio());

        Speaker updatedSpeaker = speakerRepository.save(foundSpeaker);

        return convertToResponseDto(updatedSpeaker);
    }

    @Override
    @Transactional
    public void deleteSpeaker(Long id)
    {
        Speaker foundSpeaker = speakerRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Speaker non trovato con id: " + id));

        speakerRepository.delete(foundSpeaker);
    }

    // Helper interno per mappare l'Entity nel DTO di risposta
    private SpeakerResponseDto convertToResponseDto(Speaker speaker)
    {
        SpeakerResponseDto speakerResponseDto = new SpeakerResponseDto();
        speakerResponseDto.setId(speaker.getId());
        speakerResponseDto.setName(speaker.getName());
        speakerResponseDto.setBio(speaker.getBio());

        return speakerResponseDto;
    }
}
