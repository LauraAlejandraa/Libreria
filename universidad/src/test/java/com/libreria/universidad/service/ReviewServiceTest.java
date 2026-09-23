package com.libreria.universidad.service;

import com.libreria.universidad.dto.ReviewRequestDTO;
import com.libreria.universidad.dto.ReviewResponseDTO;
import com.libreria.universidad.entity.Book;
import com.libreria.universidad.entity.Review;
import com.libreria.universidad.exception.ResourceNotFoundException;
import com.libreria.universidad.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ReviewService reviewService;

    @Captor
    private ArgumentCaptor<Review> reviewCaptor;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Clean Code", "Robert C. Martin", "9780132350884", 45.0, "Libro sobre buenas practicas");
        book.setId(1L);
    }

    @Test
    void create_savesReviewForExistingBookAndReturnsResponse() {
        ReviewRequestDTO request = request("Ana", "Muy recomendado");
        Review savedReview = new Review(book, request.getReviewerName(), request.getText());
        savedReview.setId(10L);
        LocalDateTime createdAt = LocalDateTime.of(2026, 9, 21, 10, 30);
        savedReview.setCreatedAt(createdAt);

        when(bookService.findEntityById(1L)).thenReturn(book);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewResponseDTO response = reviewService.create(1L, request);

        verify(bookService).findEntityById(1L);
        verify(reviewRepository).save(reviewCaptor.capture());
        Review persistedReview = reviewCaptor.getValue();
        assertSame(book, persistedReview.getBook());
        assertEquals("Ana", persistedReview.getReviewerName());
        assertEquals("Muy recomendado", persistedReview.getText());
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getBookId());
        assertEquals("Ana", response.getReviewerName());
        assertEquals("Muy recomendado", response.getText());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    void create_doesNotSaveWhenBookDoesNotExist() {
        ReviewRequestDTO request = request("Ana", "Muy recomendado");
        ResourceNotFoundException exception = new ResourceNotFoundException("No existe un libro con id 99");
        when(bookService.findEntityById(99L)).thenThrow(exception);

        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.create(99L, request));

        assertSame(exception, thrown);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void findByBook_returnsAllReviewsMappedToResponses() {
        Review firstReview = review(10L, "Ana", "Muy recomendado", LocalDateTime.of(2026, 9, 20, 8, 0));
        Review secondReview = review(11L, "Luis", "Explica muy bien los conceptos", LocalDateTime.of(2026, 9, 21, 9, 15));
        when(bookService.findEntityById(1L)).thenReturn(book);
        when(reviewRepository.findByBookId(1L)).thenReturn(List.of(firstReview, secondReview));

        List<ReviewResponseDTO> responses = reviewService.findByBook(1L);

        verify(bookService).findEntityById(1L);
        verify(reviewRepository).findByBookId(1L);
        assertEquals(2, responses.size());
        assertEquals(10L, responses.getFirst().getId());
        assertEquals("Ana", responses.getFirst().getReviewerName());
        assertEquals(11L, responses.get(1).getId());
        assertEquals("Explica muy bien los conceptos", responses.get(1).getText());
        assertEquals(1L, responses.get(1).getBookId());
    }

    @Test
    void findByBook_doesNotQueryReviewsWhenBookDoesNotExist() {
        ResourceNotFoundException exception = new ResourceNotFoundException("No existe un libro con id 99");
        when(bookService.findEntityById(99L)).thenThrow(exception);

        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.findByBook(99L));

        assertSame(exception, thrown);
        verify(reviewRepository, never()).findByBookId(eq(99L));
    }

    private ReviewRequestDTO request(String reviewerName, String text) {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setReviewerName(reviewerName);
        request.setText(text);
        return request;
    }

    private Review review(Long id, String reviewerName, String text, LocalDateTime createdAt) {
        Review review = new Review(book, reviewerName, text);
        review.setId(id);
        review.setCreatedAt(createdAt);
        return review;
    }
}
