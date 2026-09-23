package com.libreria.universidad.controller;

import com.libreria.universidad.dto.ReviewRequestDTO;
import com.libreria.universidad.dto.ReviewResponseDTO;
import com.libreria.universidad.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/books/{bookId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    // Historia de usuario 4 (previsualizar es responsabilidad del cliente, no de este endpoint)
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@PathVariable Long bookId,
                                                    @Valid @RequestBody ReviewRequestDTO request) {
        ReviewResponseDTO created = reviewService.create(bookId, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> findByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.findByBook(bookId));
    }
}

