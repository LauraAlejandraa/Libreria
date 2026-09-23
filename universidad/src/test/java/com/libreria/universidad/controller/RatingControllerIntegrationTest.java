package com.libreria.universidad.controller;

import com.libreria.universidad.entity.Book;
import com.libreria.universidad.entity.Rating;
import com.libreria.universidad.repository.BookRepository;
import com.libreria.universidad.repository.RatingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RatingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Test
    void create_persistsRatingAndReturnsCreatedResponse() throws Exception {
        Book book = saveBook("Libro calificado", "900000000101");

        mockMvc.perform(post("/api/books/{bookId}/ratings", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reviewerName":"Ana","score":5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.bookId").value(book.getId()))
                .andExpect(jsonPath("$.reviewerName").value("Ana"))
                .andExpect(jsonPath("$.score").value(5));

        List<Rating> ratings = ratingRepository.findByBookId(book.getId());
        assertEquals(1, ratings.size());
        assertEquals("Ana", ratings.getFirst().getReviewerName());
        assertEquals(5, ratings.getFirst().getScore());
    }

    @Test
    void findByBook_returnsOnlyRatingsForRequestedBook() throws Exception {
        Book book = saveBook("Libro solicitado", "900000000102");
        Book anotherBook = saveBook("Otro libro", "900000000103");
        ratingRepository.saveAll(List.of(
                new Rating(book, "Ana", 5),
                new Rating(book, "Luis", 3),
                new Rating(anotherBook, "Marta", 1)));

        mockMvc.perform(get("/api/books/{bookId}/ratings", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].bookId", containsInAnyOrder(
                        book.getId().intValue(), book.getId().intValue())))
                .andExpect(jsonPath("$[*].reviewerName", containsInAnyOrder("Ana", "Luis")))
                .andExpect(jsonPath("$[*].score", containsInAnyOrder(5, 3)));
    }

    @Test
    void average_returnsCalculatedAverageAndBookId() throws Exception {
        Book book = saveBook("Libro con promedio", "900000000104");
        ratingRepository.saveAll(List.of(
                new Rating(book, "Ana", 5),
                new Rating(book, "Luis", 4),
                new Rating(book, "Marta", 2)));

        mockMvc.perform(get("/api/books/{bookId}/ratings/average", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(book.getId()))
                .andExpect(jsonPath("$.averageScore").value(11.0 / 3.0));
    }

    @Test
    void average_returnsZeroWhenBookHasNoRatings() throws Exception {
        Book book = saveBook("Libro sin calificaciones", "900000000105");

        mockMvc.perform(get("/api/books/{bookId}/ratings/average", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(book.getId()))
                .andExpect(jsonPath("$.averageScore").value(0.0));
    }

    @Test
    void create_rejectsBlankReviewerAndScoreOutsideAllowedRange() throws Exception {
        Book book = saveBook("Libro validacion", "900000000106");

        mockMvc.perform(post("/api/books/{bookId}/ratings", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reviewerName":" ","score":6}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validacion"))
                .andExpect(jsonPath("$.details", containsInAnyOrder(
                        "reviewerName: El nombre de quien califica es obligatorio",
                        "score: El puntaje maximo es 5")));

        assertTrue(ratingRepository.findByBookId(book.getId()).isEmpty());
    }

    @Test
    void ratingsEndpoints_returnNotFoundWhenBookDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/books/{bookId}/ratings", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe un libro con id 999999"));

        mockMvc.perform(get("/api/books/{bookId}/ratings/average", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe un libro con id 999999"));
    }

    private Book saveBook(String title, String isbn) {
        return bookRepository.save(new Book(title, "Autor de prueba", isbn, 45.0, "Descripcion"));
    }
}
