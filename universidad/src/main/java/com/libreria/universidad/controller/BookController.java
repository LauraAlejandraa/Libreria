package com.libreria.universidad.controller;

import com.libreria.universidad.dto.BookRequestDTO;
import com.libreria.universidad.dto.BookResponseDTO;
import com.libreria.universidad.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> create(@Valid @RequestBody BookRequestDTO request) {
        BookResponseDTO created = bookService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> findAll() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> update(@PathVariable Long id,
                                                  @Valid @RequestBody BookRequestDTO request) {
        return ResponseEntity.ok(bookService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }


    // Historia de usuario 1: GET /api/books/search/simple?q=texto
    @GetMapping("/search/simple")
    public ResponseEntity<List<BookResponseDTO>> searchSimple(@RequestParam("q") String term) {
        return ResponseEntity.ok(bookService.searchSimple(term));
    }

    // Historia de usuario 2: GET /api/books/search?author=&title=&isbn=
    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDTO>> searchCombined(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String isbn) {
        return ResponseEntity.ok(bookService.searchCombined(author, title, isbn));
    }
}