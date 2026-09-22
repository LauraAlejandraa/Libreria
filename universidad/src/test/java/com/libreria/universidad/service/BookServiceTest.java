package com.libreria.universidad.service;

import com.libreria.universidad.dto.BookRequestDTO;
import com.libreria.universidad.dto.BookResponseDTO;
import com.libreria.universidad.entity.Book;
import com.libreria.universidad.exception.DuplicateResourceException;
import com.libreria.universidad.exception.ResourceNotFoundException;
import com.libreria.universidad.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    private Book cleanCode;

    @BeforeEach
    void setUp() {
        cleanCode = book(1L, "Clean Code", "Robert C. Martin", "9780132350884");
    }

    @Test
    void create_savesBookAndReturnsResponse() {
        BookRequestDTO request = request("Effective Java", "Joshua Bloch", "9780134685991");
        Book saved = book(2L, request.getTitle(), request.getAuthor(), request.getIsbn());

        when(bookRepository.findByIsbn(request.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookResponseDTO response = bookService.create(request);

        verify(bookRepository).save(bookCaptor.capture());
        assertEquals("Effective Java", bookCaptor.getValue().getTitle());
        assertEquals("Joshua Bloch", bookCaptor.getValue().getAuthor());
        assertEquals(2L, response.getId());
        assertEquals("9780134685991", response.getIsbn());
    }

    @Test
    void create_rejectsDuplicateIsbn() {
        BookRequestDTO request = request("Otro titulo", "Otro autor", cleanCode.getIsbn());
        when(bookRepository.findByIsbn(cleanCode.getIsbn())).thenReturn(Optional.of(cleanCode));

        assertThrows(DuplicateResourceException.class, () -> bookService.create(request));

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void findById_throwsWhenBookDoesNotExist() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.findById(99L));
    }

    @Test
    void update_rejectsIsbnOwnedByAnotherBook() {
        BookRequestDTO request = request("Clean Code actualizado", "Robert C. Martin", "9780134685991");
        Book effectiveJava = book(2L, "Effective Java", "Joshua Bloch", request.getIsbn());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(cleanCode));
        when(bookRepository.findByIsbn(request.getIsbn())).thenReturn(Optional.of(effectiveJava));

        assertThrows(DuplicateResourceException.class, () -> bookService.update(1L, request));

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void searchSimple_delegatesTermAndMapsMatches() {
        Book effectiveJava = book(2L, "Effective Java", "Joshua Bloch", "9780134685991");
        when(bookRepository.searchSimple("java")).thenReturn(List.of(effectiveJava));

        List<BookResponseDTO> results = bookService.searchSimple("java");

        verify(bookRepository).searchSimple("java");
        assertEquals(1, results.size());
        assertEquals("Effective Java", results.getFirst().getTitle());
        assertEquals("Joshua Bloch", results.getFirst().getAuthor());
    }

    @Test
    void searchSimple_returnsEmptyListWhenThereAreNoMatches() {
        when(bookRepository.searchSimple("inexistente")).thenReturn(List.of());

        List<BookResponseDTO> results = bookService.searchSimple("inexistente");

        assertEquals(List.of(), results);
    }

    @Test
    void searchCombined_delegatesAllOptionalCriteriaAndMapsMatches() {
        when(bookRepository.searchCombined("martin", "clean", "0884")).thenReturn(List.of(cleanCode));

        List<BookResponseDTO> results = bookService.searchCombined("martin", "clean", "0884");

        verify(bookRepository).searchCombined("martin", "clean", "0884");
        assertEquals(1, results.size());
        assertEquals(1L, results.getFirst().getId());
        assertEquals("Clean Code", results.getFirst().getTitle());
    }

    @Test
    void searchCombined_passesNullForOmittedCriteria() {
        when(bookRepository.searchCombined(eq("Martin"), eq(null), eq(null))).thenReturn(List.of(cleanCode));

        List<BookResponseDTO> results = bookService.searchCombined("Martin", null, null);

        verify(bookRepository).searchCombined("Martin", null, null);
        assertEquals(List.of("Clean Code"), results.stream().map(BookResponseDTO::getTitle).toList());
    }

    private BookRequestDTO request(String title, String author, String isbn) {
        BookRequestDTO request = new BookRequestDTO();
        request.setTitle(title);
        request.setAuthor(author);
        request.setIsbn(isbn);
        request.setPrice(45.0);
        request.setDescription("Descripcion de prueba");
        return request;
    }

    private Book book(Long id, String title, String author, String isbn) {
        Book book = new Book(title, author, isbn, 45.0, "Descripcion de prueba");
        book.setId(id);
        return book;
    }
}
