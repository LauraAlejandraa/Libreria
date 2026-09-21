package com.libreria.universidad.dto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class ReviewResponseDTO {
    private Long id;
    private Long bookId;
    private String reviewerName;
    private String text;
    private LocalDateTime createdAt;
    public ReviewResponseDTO() {
    }
    public ReviewResponseDTO(Long id, Long bookId, String reviewerName, String text, LocalDateTime createdAt){
        this.id = id;
        this.bookId = bookId;
        this.reviewerName = reviewerName;
        this.text = text;
        this.createdAt = createdAt;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getBookId() {
        return bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
