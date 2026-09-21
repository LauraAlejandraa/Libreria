package com.libreria.universidad.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewRequestDTO {
    @NotBlank(message = "El nombre de quien resena es obligatorio")
    private String reviewerName;
    @NotBlank(message = "El texto de la resena es obligatorio")
    private String text;
    public String getReviewerName() {
        return reviewerName;
    }
    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}

