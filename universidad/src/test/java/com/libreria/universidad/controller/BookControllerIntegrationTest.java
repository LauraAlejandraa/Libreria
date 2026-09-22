package com.libreria.universidad.controller;

import com.libreria.universidad.entity.Book;
import com.libreria.universidad.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void searchSimple_findsBooksByPartialTitleIgnoringCase() throws Exception {
        Book effectiveJava = saveBook("Guia Zafiro", "Joshua Bloch", "900000000001");
        Book javaPuzzlers = saveBook("Patrones Zafiro", "Joshua Bloch", "900000000002");
        saveBook("Python Cookbook", "David Beazley", "900000000003");

        mockMvc.perform(get("/api/books/search/simple").param("q", "zAfIrO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        effectiveJava.getId().intValue(), javaPuzzlers.getId().intValue())))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Guia Zafiro", "Patrones Zafiro")));
    }

    @Test
    void searchSimple_findsBooksByPartialAuthorIgnoringCase() throws Exception {
        Book cleanCode = saveBook("Clean Code de prueba", "Aurelia Quasar", "900000000004");
        Book cleanArchitecture = saveBook("Clean Architecture de prueba", "AURELIA QUASAR", "900000000005");
        saveBook("Refactoring de prueba", "Martin Fowler", "900000000006");

        mockMvc.perform(get("/api/books/search/simple").param("q", "aUrElIa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        cleanCode.getId().intValue(), cleanArchitecture.getId().intValue())));
    }

    @Test
    void searchSimple_returnsEmptyArrayWhenNothingMatches() throws Exception {
        mockMvc.perform(get("/api/books/search/simple").param("q", "termino-no-encontrado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchCombined_appliesAuthorTitleAndIsbnTogether() throws Exception {
        Book expected = saveBook("Arquitectura Limpia", "Robert Martin", "900000000007");
        saveBook("Arquitectura Hexagonal", "Robert Martin", "900000000008");
        saveBook("Arquitectura Limpia", "Martin Fowler", "900000000009");
        saveBook("Clean Code", "Robert Martin", "900000000010");

        mockMvc.perform(get("/api/books/search")
                        .param("author", "ROBERT")
                        .param("title", "limpia")
                        .param("isbn", "000007"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(expected.getId()))
                .andExpect(jsonPath("$[0].title").value("Arquitectura Limpia"));
    }

    @Test
    void searchCombined_allowsOmittedCriteria() throws Exception {
        Book firstMatch = saveBook("Patrones de Diseno", "Gamma", "900000000011");
        Book secondMatch = saveBook("Patrones de Integracion", "Hohpe", "900000000012");
        saveBook("Refactoring", "Martin Fowler", "900000000013");

        mockMvc.perform(get("/api/books/search").param("title", "patrones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        firstMatch.getId().intValue(), secondMatch.getId().intValue())));
    }

    @Test
    void searchCombined_returnsEmptyArrayWhenCriteriaHaveNoCommonMatch() throws Exception {
        saveBook("DDD", "Eric Evans", "900000000014");
        saveBook("Clean Code avanzado", "Robert Martin", "900000000015");

        mockMvc.perform(get("/api/books/search")
                        .param("author", "Eric")
                        .param("title", "Clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private Book saveBook(String title, String author, String isbn) {
        return bookRepository.save(new Book(title, author, isbn, 45.0, "Descripcion de prueba"));
    }
}
