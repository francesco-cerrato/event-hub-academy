package com.academy.eventhub.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class EventResponseDto
{
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private BigDecimal price;
    private BigDecimal vipPrice;
    private String organizerUsername;
    private VenueResponseDto venue;
    private Set<String> tags;

    public EventResponseDto()
    {}

    public EventResponseDto(Long id, String title, String description, LocalDateTime eventDate,
                            BigDecimal price, BigDecimal vipPrice, String organizerUsername,
                            VenueResponseDto venue, Set<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.price = price;
        this.vipPrice = vipPrice;
        this.organizerUsername = organizerUsername;
        this.venue = venue;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(BigDecimal vipPrice) {
        this.vipPrice = vipPrice;
    }

    public String getOrganizerUsername() {
        return organizerUsername;
    }

    public void setOrganizerUsername(String organizerUsername) {
        this.organizerUsername = organizerUsername;
    }

    public VenueResponseDto getVenue() {
        return venue;
    }

    public void setVenue(VenueResponseDto venue) {
        this.venue = venue;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
