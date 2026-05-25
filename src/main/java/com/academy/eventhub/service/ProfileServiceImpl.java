package com.academy.eventhub.service;

import com.academy.eventhub.dto.ProfileResponseDto;
import com.academy.eventhub.dto.ProfileUpdateDto;
import com.academy.eventhub.entity.Profile;
import com.academy.eventhub.entity.User;
import com.academy.eventhub.exception.ResourceNotFoundException;
import com.academy.eventhub.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService{

    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository)
    {
        this.profileRepository = profileRepository;
    }

    @Override
    public List<ProfileResponseDto> getAllProfiles() {
        List<Profile> profileList = profileRepository.findAll();
        List<ProfileResponseDto> dtoList = new ArrayList<>();

        for (Profile profile : profileList) {
            ProfileResponseDto dto = convertToResponseDto(profile);
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public ProfileResponseDto getProfileById(Long id) {
        Profile foundProfile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profilo non trovato con id: " + id));

        return convertToResponseDto(foundProfile);
    }

    @Override
    public ProfileResponseDto updateProfile(Long id, ProfileUpdateDto inputDto)
    {
        Profile profileToUpdate = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profilo non trovato con id: " + id));

        profileToUpdate.setFirstName(inputDto.getFirstName());
        profileToUpdate.setLastName(inputDto.getLastName());
        profileToUpdate.setBio(inputDto.getBio());
        profileToUpdate.setCity(inputDto.getCity());
        profileToUpdate.setAvatarUrl(inputDto.getAvatarUrl());

        Profile updatedProfile = profileRepository.save(profileToUpdate);

        return convertToResponseDto(updatedProfile);
    }

    @Override
    public ProfileResponseDto getProfileByUsername(String username)
    {
        Profile foundProfile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Profilo non trovato con username: " + username));

        return convertToResponseDto(foundProfile);
    }

    @Override
    public ProfileResponseDto updateProfileByUsername(String username, ProfileUpdateDto inputDto)
    {
        Profile profileToUpdate = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Profilo non trovato con username: " + username));

        profileToUpdate.setFirstName(inputDto.getFirstName());
        profileToUpdate.setLastName(inputDto.getLastName());
        profileToUpdate.setBio(inputDto.getBio());
        profileToUpdate.setCity(inputDto.getCity());
        profileToUpdate.setAvatarUrl(inputDto.getAvatarUrl());

        Profile updatedProfile = profileRepository.save(profileToUpdate);

        return convertToResponseDto(updatedProfile);
    }

    private ProfileResponseDto convertToResponseDto(Profile profile)
    {
        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        profileResponseDto.setId(profile.getId());
        profileResponseDto.setFirstName(profile.getFirstName());
        profileResponseDto.setLastName(profile.getLastName());
        profileResponseDto.setBio(profile.getBio());
        profileResponseDto.setCity(profile.getCity());
        profileResponseDto.setAvatarUrl(profile.getAvatarUrl());

        /*
            Previene crash improvvisi nel caso in cui un profilo sul database
            non fosse momentaneamente agganciato a nessun utente.
         */
        if (profile.getUser() != null)
        {
            // Per evitare che Spring vada in ciclo infinto quando trasforma l'oggetto in JSON
            profileResponseDto.setUserId(profile.getUser().getId());
        }

        return profileResponseDto;
    }
}
