package com.libreria.universidad.service;
import com.libreria.universidad.dto.ReviewRequestDTO;
import com.libreria.universidad.dto.ReviewResponseDTO;
import com.libreria.universidad.entity.Book;
import com.libreria.universidad.entity.Review;
import com.libreria.universidad.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookService bookService;
    public ReviewService(ReviewRepository reviewRepository, BookService bookService) {
        this.reviewRepository = reviewRepository;
        this.bookService = bookService;
    }
    // Historia de usuario 4 (el "preview" se maneja en el cliente, ver DECISION TECNICA 2)
    public ReviewResponseDTO create(Long bookId, ReviewRequestDTO request) {
        Book book = bookService.findEntityById(bookId);
        Review review = new Review(book, request.getReviewerName(), request.getText());
        Review saved = reviewRepository.save(review);
        return toResponseDTO(saved);
    }
    public List<ReviewResponseDTO> findByBook(Long bookId) {
        bookService.findEntityById(bookId);
        return reviewRepository.findByBookId(bookId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    private ReviewResponseDTO toResponseDTO(Review review) {
        return new ReviewResponseDTO(review.getId(), review.getBook().getId(),
                review.getReviewerName(), review.getText(), review.getCreatedAt());
    }
    }

