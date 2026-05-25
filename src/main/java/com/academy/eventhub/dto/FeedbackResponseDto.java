package com.academy.eventhub.dto;

public class FeedbackResponseDto
{
    private Long id;
    private Integer rating;
    private String comment;
    private String username; // Per mostrare chi ha lasciato il feedback

    public FeedbackResponseDto()
    {}

    public FeedbackResponseDto(Long id, Integer rating, String comment, String username) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
