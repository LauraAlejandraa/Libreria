package com.libreria.universidad.controller;
import com.libreria.universidad.dto.RatingRequestDTO;
import com.libreria.universidad.dto.RatingResponseDTO;
import com.libreria.universidad.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/books/{bookId}/ratings")
public class RatingController {
    private final RatingService ratingService;
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }
    // Historia de usuario 3
    @PostMapping
    public ResponseEntity<RatingResponseDTO> create(@PathVariable Long bookId,
                                                    @Valid @RequestBody RatingRequestDTO request) {
        RatingResponseDTO created = ratingService.create(bookId, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<RatingResponseDTO>> findByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(ratingService.findByBook(bookId));
    }
    @GetMapping("/average")
    public ResponseEntity<Map<String, Object>> average(@PathVariable Long bookId) {
        Double average = ratingService.averageForBook(bookId);
        return ResponseEntity.ok(Map.of("bookId", bookId, "averageScore", average));
    }
}
