package com.libreria.universidad.service;

import com.libreria.universidad.dto.RatingRequestDTO;
import com.libreria.universidad.dto.RatingResponseDTO;
import com.libreria.universidad.entity.Book;
import com.libreria.universidad.entity.Rating;
import com.libreria.universidad.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private RatingService ratingService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Clean Code", "Robert C. Martin", "9780132350884", 39.99, "desc");
        book.setId(1L);
    }

    @Test
    void deberiaCrearCalificacionValida() {
        RatingRequestDTO request = new RatingRequestDTO();
        request.setReviewerName("Ana Torres");
        request.setScore(5);

        Rating saved = new Rating(book, "Ana Torres", 5);
        saved.setId(1L);

        when(bookService.findEntityById(1L)).thenReturn(book);
        when(ratingRepository.save(any(Rating.class))).thenReturn(saved);

        RatingResponseDTO response = ratingService.create(1L, request);

        assertEquals(5, response.getScore());
        assertEquals(1L, response.getBookId());
        assertEquals("Ana Torres", response.getReviewerName());
    }

    @Test
    void promedioDeberiaSerCeroSiNoHayCalificaciones() {
        when(bookService.findEntityById(1L)).thenReturn(book);
        when(ratingRepository.findAverageScoreByBookId(1L)).thenReturn(null);

        Double average = ratingService.averageForBook(1L);

        assertEquals(0.0, average);
    }
}