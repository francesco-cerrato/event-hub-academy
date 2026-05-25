package com.academy.eventhub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FeedbackRequestDto
{
    @NotNull(message = "Il rating è obbligatorio")
    @Min(value = 1, message = "Il rating minimo è 1")
    @Max(value = 5, message = "Il rating massimo è 5")
    private Integer rating;

    private String comment;

    public FeedbackRequestDto()
    {}

    public FeedbackRequestDto(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
