package com.libreria.universidad.service;
import com.libreria.universidad.dto.RatingRequestDTO;
import com.libreria.universidad.dto.RatingResponseDTO;
import com.libreria.universidad.entity.Book;
import com.libreria.universidad.entity.Rating;
import com.libreria.universidad.repository.RatingRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final BookService bookService;
    public RatingService(RatingRepository ratingRepository, BookService bookService) {
        this.ratingRepository = ratingRepository;
        this.bookService = bookService;
    }
    // Historia de usuario 3
    public RatingResponseDTO create(Long bookId, RatingRequestDTO request) {
        Book book = bookService.findEntityById(bookId);
        Rating rating = new Rating(book, request.getReviewerName(), request.getScore());
        Rating saved = ratingRepository.save(rating);
        return toResponseDTO(saved);
    }
    public List<RatingResponseDTO> findByBook(Long bookId) {
        bookService.findEntityById(bookId);
        return ratingRepository.findByBookId(bookId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    public Double averageForBook(Long bookId) {
        bookService.findEntityById(bookId);
        Double average = ratingRepository.findAverageScoreByBookId(bookId);
        return average == null ? 0.0 : average;
    }
    private RatingResponseDTO toResponseDTO(Rating rating) {
        return new RatingResponseDTO(rating.getId(), rating.getBook().getId(),
                rating.getReviewerName(), rating.getScore(), rating.getCreatedAt());
    }
}