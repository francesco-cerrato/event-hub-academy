package com.academy.eventhub.service;

import com.academy.eventhub.dto.VenueRequestDto;
import com.academy.eventhub.dto.VenueResponseDto;
import com.academy.eventhub.entity.Venue;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.VenueRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VenueServiceImpl implements VenueService
{
    private final VenueRepository venueRepository;

    @Autowired
    public VenueServiceImpl(VenueRepository venueRepository)
    {
        this.venueRepository = venueRepository;
    }

    @Override
    @Transactional
    public VenueResponseDto createVenue(VenueRequestDto venueRequestDto)
    {
        Venue newVenue = new Venue();
        newVenue.setName(venueRequestDto.getName());
        newVenue.setAddress(venueRequestDto.getAddress());
        newVenue.setCapacity(venueRequestDto.getCapacity());

        Venue savedVenue = venueRepository.save(newVenue);
        return convertToResponseDto(savedVenue);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponseDto getVenueById(Long id)
    {
        Venue foundVenue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede non trovata con id: " + id));
        return convertToResponseDto(foundVenue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponseDto> getAllVenues()
    {
        List<Venue> venueList = venueRepository.findAll();

        List<VenueResponseDto> dtoList = new ArrayList<>();

        for (Venue venue : venueList)
        {
            VenueResponseDto dto = convertToResponseDto(venue);

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    @Transactional
    public VenueResponseDto updateVenue(Long id, VenueRequestDto venueRequestDto)
    {
        Venue foundVenue = venueRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Sede non trovata con id: " + id));

        foundVenue.setName(venueRequestDto.getName());
        foundVenue.setAddress(venueRequestDto.getAddress());
        foundVenue.setCapacity(venueRequestDto.getCapacity());

        Venue updatedVenue = venueRepository.save(foundVenue);

        return convertToResponseDto(updatedVenue);
    }

    @Override
    @Transactional
    public void deleteVenue(Long id)
    {
        Venue foundVenue = venueRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Sede non trovata con id: " + id));

        venueRepository.delete(foundVenue);

    }

    public VenueResponseDto convertToResponseDto(Venue venue)
    {
        VenueResponseDto venueResponseDto = new VenueResponseDto();
        venueResponseDto.setId(venue.getId());
        venueResponseDto.setName(venue.getName());
        venueResponseDto.setAddress(venue.getAddress());
        venueResponseDto.setCapacity(venue.getCapacity());

        return venueResponseDto;
    }
}
