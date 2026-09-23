package com.libreria.universidad.config;

import com.libreria.universidad.entity.Book;
import com.libreria.universidad.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;

    public DataSeeder(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            bookRepository.save(new Book("Effective Java", "Joshua Bloch", "9780134685991", 45.99,
                    "Buenas practicas para programar en Java."));
            bookRepository.save(new Book("Clean Code", "Robert C. Martin", "9780132350884", 39.99,
                    "Principios de codigo limpio y mantenible."));
            bookRepository.save(new Book("User Stories Applied", "Mike Cohn", "9780321205681", 34.99,
                    "Como escribir historias de usuario para desarrollo agil."));
            bookRepository.save(new Book("Domain-Driven Design", "Eric Evans", "9780321125217", 54.99,
                    "Diseno de software centrado en el dominio del negocio."));
            bookRepository.save(new Book("Spring in Action", "Craig Walls", "9781617294945", 44.99,
                    "Guia practica del framework Spring."));
        }
    }
}