package com.libreria.universidad.entity;

import com.uniandes.southcoast.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    // Historia de usuario 1: busqueda simple en autor O titulo
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Book> searchSimple(@Param("term") String term);

    // Historia de usuario 2: busqueda combinada, cada campo es opcional
    @Query("SELECT b FROM Book b WHERE " +
            "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:isbn IS NULL OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :isbn, '%')))")
    List<Book> searchCombined(@Param("author") String author,
                              @Param("title") String title,
                              @Param("isbn") String isbn);
}