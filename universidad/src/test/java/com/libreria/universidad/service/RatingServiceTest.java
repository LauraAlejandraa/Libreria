package com.libreria.universidad.service;

import com.libreria.universidad.dto.RatingRequestDTO;
import com.libreria.universidad.dto.RatingResponseDTO;
import com.libreria.universidad.entity.Book;
import com.libreria.universidad.entity.Rating;
import com.libreria.universidad.exception.ResourceNotFoundException;
import com.libreria.universidad.repository.RatingRepository;
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
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private RatingService ratingService;

    @Captor
    private ArgumentCaptor<Rating> ratingCaptor;

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

        verify(ratingRepository).save(ratingCaptor.capture());
        assertSame(book, ratingCaptor.getValue().getBook());
        assertEquals("Ana Torres", ratingCaptor.getValue().getReviewerName());
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

    @Test
    void findByBook_returnsRatingsMappedToResponses() {
        Rating first = rating(10L, "Ana", 5, LocalDateTime.of(2026, 9, 20, 10, 0));
        Rating second = rating(11L, "Luis", 3, LocalDateTime.of(2026, 9, 21, 11, 30));
        when(bookService.findEntityById(1L)).thenReturn(book);
        when(ratingRepository.findByBookId(1L)).thenReturn(List.of(first, second));

        List<RatingResponseDTO> ratings = ratingService.findByBook(1L);

        verify(bookService).findEntityById(1L);
        verify(ratingRepository).findByBookId(1L);
        assertEquals(2, ratings.size());
        assertEquals(10L, ratings.getFirst().getId());
        assertEquals("Ana", ratings.getFirst().getReviewerName());
        assertEquals(3, ratings.get(1).getScore());
    }

    @Test
    void findByBook_doesNotQueryRatingsWhenBookDoesNotExist() {
        ResourceNotFoundException exception = new ResourceNotFoundException("No existe un libro con id 99");
        when(bookService.findEntityById(99L)).thenThrow(exception);

        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class, () -> ratingService.findByBook(99L));

        assertSame(exception, thrown);
        verify(ratingRepository, never()).findByBookId(eq(99L));
    }

    @Test
    void averageForBook_returnsRepositoryAverage() {
        when(bookService.findEntityById(1L)).thenReturn(book);
        when(ratingRepository.findAverageScoreByBookId(1L)).thenReturn(4.25);

        Double average = ratingService.averageForBook(1L);

        assertEquals(4.25, average);
        verify(ratingRepository).findAverageScoreByBookId(1L);
    }

    @Test
    void averageForBook_doesNotQueryAverageWhenBookDoesNotExist() {
        ResourceNotFoundException exception = new ResourceNotFoundException("No existe un libro con id 99");
        when(bookService.findEntityById(99L)).thenThrow(exception);

        assertThrows(ResourceNotFoundException.class, () -> ratingService.averageForBook(99L));

        verify(ratingRepository, never()).findAverageScoreByBookId(99L);
    }

    private Rating rating(Long id, String reviewerName, int score, LocalDateTime createdAt) {
        Rating rating = new Rating(book, reviewerName, score);
        rating.setId(id);
        rating.setCreatedAt(createdAt);
        return rating;
    }
}
