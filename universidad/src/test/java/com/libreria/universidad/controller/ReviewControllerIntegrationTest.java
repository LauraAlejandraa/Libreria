package com.libreria.universidad.controller;

import com.libreria.universidad.entity.Book;
import com.libreria.universidad.entity.Review;
import com.libreria.universidad.repository.BookRepository;
import com.libreria.universidad.repository.ReviewRepository;
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
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void create_persistsReviewAndReturnsCreatedResponse() throws Exception {
        Book book = saveBook("Clean Code", "9780132350885");

        mockMvc.perform(post("/api/books/{bookId}/reviews", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reviewerName":"Ana","text":"Muy recomendado"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.bookId").value(book.getId()))
                .andExpect(jsonPath("$.reviewerName").value("Ana"))
                .andExpect(jsonPath("$.text").value("Muy recomendado"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        List<Review> savedReviews = reviewRepository.findByBookId(book.getId());
        assertEquals(1, savedReviews.size());
        assertEquals("Ana", savedReviews.getFirst().getReviewerName());
        assertEquals("Muy recomendado", savedReviews.getFirst().getText());
    }

    @Test
    void findByBook_returnsOnlyReviewsForRequestedBook() throws Exception {
        Book requestedBook = saveBook("Effective Java", "9780134685992");
        Book anotherBook = saveBook("Domain-Driven Design", "9780321125218");
        reviewRepository.saveAll(List.of(
                new Review(requestedBook, "Ana", "Claro y practico"),
                new Review(requestedBook, "Luis", "Excelente introduccion"),
                new Review(anotherBook, "Marta", "No debe aparecer")));

        mockMvc.perform(get("/api/books/{bookId}/reviews", requestedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].bookId", containsInAnyOrder(
                        requestedBook.getId().intValue(), requestedBook.getId().intValue())))
                .andExpect(jsonPath("$[*].reviewerName", containsInAnyOrder("Ana", "Luis")))
                .andExpect(jsonPath("$[*].text", containsInAnyOrder(
                        "Claro y practico", "Excelente introduccion")));
    }

    @Test
    void create_rejectsBlankRequiredFields() throws Exception {
        Book book = saveBook("Refactoring", "9780201485678");

        mockMvc.perform(post("/api/books/{bookId}/reviews", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"reviewerName":" ","text":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validacion"))
                .andExpect(jsonPath("$.details", containsInAnyOrder(
                        "reviewerName: El nombre de quien resena es obligatorio",
                        "text: El texto de la resena es obligatorio")));

        assertTrue(reviewRepository.findByBookId(book.getId()).isEmpty());
    }

    @Test
    void findByBook_returnsNotFoundErrorWhenBookDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/books/{bookId}/reviews", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No existe un libro con id 999999"))
                .andExpect(jsonPath("$.details", hasSize(0)));
    }

    private Book saveBook(String title, String isbn) {
        return bookRepository.save(new Book(title, "Autor de prueba", isbn, 40.0, "Descripcion"));
    }
}
