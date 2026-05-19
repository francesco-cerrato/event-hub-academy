package com.academy.eventhub.controller;

import com.academy.eventhub.dto.ProfileResponseDto;
import com.academy.eventhub.dto.ProfileUpdateDto;
import com.academy.eventhub.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController
{
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService)
    {
        this.profileService = profileService;
    }

    /*
        Endpoint per visualizzare il proprio profilo
     */
    @GetMapping("/me")
    public ResponseEntity<ProfileResponseDto> getMyProfile(Principal principal)
    {
        String loggedUsername = principal.getName();

        ProfileResponseDto myProfile = profileService.getProfileByUsername(loggedUsername);

        return ResponseEntity.ok(myProfile);
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponseDto> updateMyProfile (Principal principal,
                                                               @Valid @RequestBody ProfileUpdateDto inputDto)
    {
        String loggedUsername = principal.getName();

        ProfileResponseDto updatedProfile = profileService.updateProfileByUsername(loggedUsername, inputDto);

        return ResponseEntity.ok(updatedProfile);
    }
}
