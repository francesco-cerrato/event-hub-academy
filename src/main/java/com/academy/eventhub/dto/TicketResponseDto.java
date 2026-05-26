package com.academy.eventhub.dto;

import com.academy.eventhub.entity.TicketStatus;
import com.academy.eventhub.entity.TicketType;
import com.academy.eventhub.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketResponseDto
{
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String username;
    private TicketType type;
    private BigDecimal pricePaid;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime eventDate;

    public TicketResponseDto()
    {}

    public TicketResponseDto(Long id, Long event_id, String eventTitle, String username,
                             TicketType type, BigDecimal pricePaid,
                             TicketStatus status, LocalDateTime createdAt, LocalDateTime eventDate) {
        this.id = id;
        this.eventId = event_id;
        this.eventTitle = eventTitle;
        this.username = username;
        this.type = type;
        this.pricePaid = pricePaid;
        this.status = status;
        this.createdAt = createdAt;
        this.eventDate = eventDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEvent_id() {
        return eventId;
    }

    public void setEvent_id(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public BigDecimal getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(BigDecimal pricePaid) {
        this.pricePaid = pricePaid;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}
