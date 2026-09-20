package com.libreria.universidad.service;

import com.libreria.universidad.dto.BookRequestDTO;
import com.libreria.universidad.dto.BookResponseDTO;
import com.libreria.universidad.entity.Book;
import com.libreria.universidad.exception.DuplicateResourceException;
import com.libreria.universidad..exception.ResourceNotFoundException;
import com.libreria.universidad.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponseDTO create(BookRequestDTO request) {
        bookRepository.findByIsbn(request.getIsbn()).ifPresent(b -> {
            throw new DuplicateResourceException("Ya existe un libro con el ISBN " + request.getIsbn());
        });
        Book book = new Book(request.getTitle(), request.getAuthor(), request.getIsbn(),
                request.getPrice(), request.getDescription());
        Book saved = bookRepository.save(book);
        return toResponseDTO(saved);
    }

    public List<BookResponseDTO> findAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public BookResponseDTO findById(Long id) {
        Book book = findEntityById(id);
        return toResponseDTO(book);
    }

    public Book findEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un libro con id " + id));
    }

    public BookResponseDTO update(Long id, BookRequestDTO request) {
        Book book = findEntityById(id);

        bookRepository.findByIsbn(request.getIsbn())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(b -> {
                    throw new DuplicateResourceException("Ya existe otro libro con el ISBN " + request.getIsbn());
                });

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());

        Book updated = bookRepository.save(book);
        return toResponseDTO(updated);
    }

    public void delete(Long id) {
        Book book = findEntityById(id);
        bookRepository.delete(book);
    }

    // Historia de usuario 1
    public List<BookResponseDTO> searchSimple(String term) {
        return bookRepository.searchSimple(term).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Historia de usuario 2
    public List<BookResponseDTO> searchCombined(String author, String title, String isbn) {
        return bookRepository.searchCombined(author, title, isbn).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private BookResponseDTO toResponseDTO(Book book) {
        return new BookResponseDTO(book.getId(), book.getTitle(), book.getAuthor(),
                book.getIsbn(), book.getPrice(), book.getDescription());
    }
}