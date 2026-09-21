package com.libreria.universidad.dto;
import java.time.LocalDateTime;
public class RatingResponseDTO {
    private Long id;
    private Long bookId;
    private String reviewerName;
    private Integer score;
    private LocalDateTime createdAt;
    public RatingResponseDTO() {
    }
    public RatingResponseDTO(Long id, Long bookId, String reviewerName, Integer score, LocalDateTime created){this.id = id;
    this.bookId = bookId;
    this.reviewerName = reviewerName;
        this.score = score;
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
    public Integer getScore() {
        return score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}