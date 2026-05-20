package com.academy.eventhub.service;

import com.academy.eventhub.dto.VenueRequestDto;
import com.academy.eventhub.dto.VenueResponseDto;

import java.util.List;

public interface VenueService
{
    VenueResponseDto createVenue(VenueRequestDto venueRequestDto);
    VenueResponseDto getVenueById(Long id);
    List<VenueResponseDto> getAllVenues();
    VenueResponseDto updateVenue(Long id, VenueRequestDto venueRequestDto);
    void deleteVenue(Long id);
}
