package edu.eci.dosw.DOSW_Library.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import edu.eci.dosw.DOSW_Library.persistence.relational.dao.JpaBookDao;
import edu.eci.dosw.DOSW_Library.repository.BookRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("relational")
@Testcontainers(disabledWithoutDocker = true)
class PostgreSqlBookPersistenceIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("security.jwt.secret", () -> "integration-test-secret-integration-test-secret");
    }

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JpaBookDao jpaBookDao;

    @BeforeEach
    void cleanData() {
        jpaBookDao.deleteAll();
    }

    @Test
    void shouldCreateAndUpdateBookPersistingChangesInPostgreSql() {
        String isbn = "IT-" + UUID.randomUUID();
        Book created = bookService.createBook(new Book(null, "Domain Driven Design", "Eric Evans", isbn, 9, 9));

        assertNotNull(created.getId());
        Book persistedAfterCreate = bookRepository.findById(created.getId()).orElseThrow();
        assertEquals(9, persistedAfterCreate.getAvailableStock());
        assertEquals("Domain Driven Design", persistedAfterCreate.getTitle());

        Book update = new Book(null, "Domain Driven Design (2nd)", "Eric Evans", isbn, 10, 8);
        Book updated = bookService.updateBook(created.getId(), update);

        assertEquals(10, updated.getTotalStock());
        assertEquals(8, updated.getAvailableStock());

        Book persistedAfterUpdate = bookRepository.findById(created.getId()).orElseThrow();
        assertEquals("Domain Driven Design (2nd)", persistedAfterUpdate.getTitle());
        assertEquals(10, persistedAfterUpdate.getTotalStock());
        assertEquals(8, persistedAfterUpdate.getAvailableStock());
    }
}
