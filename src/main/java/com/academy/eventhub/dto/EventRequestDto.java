package com.academy.eventhub.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class EventRequestDto
{
    @NotBlank(message = "Il titolo dell'evento è obbligatorio")
    @Size(max = 100, message = "Il titolo non può superare i 100 caratteri")
    private String title;

    private String description;

    @NotNull(message = "La data e l'ora dell'evento sono obbligatorie")
    @Future(message = "La data dell'evento deve essere nel futuro")
    private LocalDateTime eventDate;

    @NotNull(message = "Il prezzo standard è obbligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "Il prezzo standard non può essere negativo")
    private BigDecimal price;

    @NotNull(message = "Il prezzo VIP è obbligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "Il prezzo VIP non può essere negativo")
    private BigDecimal vipPrice;

    @NotNull(message = "L'ID della sede è obbligatorio")
    private Long venueId;

    // Un set di ID dei tag associati (opzionale, può essere vuoto ma non null)
    private Set<Long> tagIds;

    private Set<Long> speakerIds;

    public EventRequestDto()
    {}

    public EventRequestDto(String title, String description, LocalDateTime eventDate,
                           BigDecimal price, BigDecimal vipPrice, Long venueId, Set<Long> tagIds,
                           Set<Long> speakerIds) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.price = price;
        this.vipPrice = vipPrice;
        this.venueId = venueId;
        this.tagIds = tagIds;
        this.speakerIds = speakerIds;
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

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public Set<Long> getSpeakerIds() {
        return speakerIds;
    }

    public void setSpeakerIds(Set<Long> speakerIds) {
        this.speakerIds = speakerIds;
    }
}
