package com.academy.eventhub.dto;

public class ProfileResponseDto
{
    private Long id;
    private String firstName;
    private String lastName;
    private String bio;
    private String city;
    private String avatarUrl;
    // Forniamo solo l'ID dell'utente associato, senza esporre tutto l'oggetto
    private Long userId;

    public ProfileResponseDto()
    {}

    public ProfileResponseDto(Long id, String firstName, String lastName, String bio, String city, String avatarUrl, Long userId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.city = city;
        this.avatarUrl = avatarUrl;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
