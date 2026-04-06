package edu.eci.dosw.DOSW_Library.persistence.relational;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.relational.dao.JpaBookDao;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.BookEntity;
import edu.eci.dosw.DOSW_Library.persistence.relational.mapper.BookPersistenceMapper;
import edu.eci.dosw.DOSW_Library.persistence.relational.repository.BookRepositoryJpaImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookJpaPersistenceTest {

    @Mock
    private JpaBookDao jpaBookDao;

    @Mock
    private BookPersistenceMapper mapper;

    @InjectMocks
    private BookRepositoryJpaImpl repository;

    @Test
    void findAll_returnsAllBooks() {
        BookEntity entity = new BookEntity();
        Book domain = new Book(1L, "Clean Code", "Martin", "ISBN-1", 5, 3);
        when(jpaBookDao.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Book> result = repository.findAll();

        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void findById_existingId_returnsBook() {
        BookEntity entity = new BookEntity();
        Book domain = new Book(1L, "Clean Code", "Martin", "ISBN-1", 5, 3);
        when(jpaBookDao.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Book> result = repository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findById_missingId_returnsEmpty() {
        when(jpaBookDao.findById(99L)).thenReturn(Optional.empty());

        Optional<Book> result = repository.findById(99L);

        assertTrue(result.isEmpty());
    }
}
