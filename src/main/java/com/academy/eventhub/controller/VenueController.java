package com.academy.eventhub.controller;

import com.academy.eventhub.dto.VenueRequestDto;
import com.academy.eventhub.dto.VenueResponseDto;
import com.academy.eventhub.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*
    Nel SecurityConfig è stato configurato un accesso limitato
    alla rotta "admin" esclusivamente agli utenti di ruolo ADMIN
    Codice: .requestMatchers("/admin/**").hasRole("ADMIN")
 */
@RequestMapping("/admin/venues")
@RestController
public class VenueController
{

    private final VenueService venueService;

    @Autowired
    public VenueController(VenueService venueService)
    {
        this.venueService = venueService;
    }


    @PostMapping
    public ResponseEntity<VenueResponseDto> createVenue(@Valid @RequestBody VenueRequestDto venueRequestDto)
    {
        VenueResponseDto createdVenue = venueService.createVenue(venueRequestDto);
        return new ResponseEntity<>(createdVenue, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDto> getVenueById(@PathVariable Long id)
    {
        VenueResponseDto foundVenue = venueService.getVenueById(id);
        return ResponseEntity.ok(foundVenue);
    }

    @GetMapping
    public ResponseEntity<List<VenueResponseDto>> getAllVenues()
    {
        List<VenueResponseDto> venueList = venueService.getAllVenues();
        return ResponseEntity.ok(venueList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueResponseDto> updateVenue(@PathVariable Long id,
                                                        @Valid @RequestBody VenueRequestDto venueRequestDto)
    {
        VenueResponseDto updatedVenue = venueService.updateVenue(id, venueRequestDto);
        return  ResponseEntity.ok(updatedVenue);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id)
    {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}
