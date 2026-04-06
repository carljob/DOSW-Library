package edu.eci.dosw.DOSW_Library.persistence.nonrelational;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.BookDocument;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.mapper.BookDocumentMapper;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository.BookRepositoryMongoImpl;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository.MongoBookRepository;
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
class BookMongoPersistenceTest {

    @Mock
    private MongoBookRepository mongoBookRepository;

    @Mock
    private BookDocumentMapper mapper;

    @InjectMocks
    private BookRepositoryMongoImpl repository;

    @Test
    void findAll_returnsAllBooks() {
        BookDocument doc = new BookDocument();
        Book domain = new Book(null, "Clean Code", "Martin", "ISBN-1", 5, 3);
        when(mongoBookRepository.findAll()).thenReturn(List.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        List<Book> result = repository.findAll();

        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void findById_existingId_returnsBook() {
        BookDocument doc = new BookDocument();
        Book domain = new Book(null, "Clean Code", "Martin", "ISBN-1", 5, 3);
        when(mongoBookRepository.findById("1")).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        Optional<Book> result = repository.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void findById_missingId_returnsEmpty() {
        when(mongoBookRepository.findById("99")).thenReturn(Optional.empty());

        Optional<Book> result = repository.findById(99L);

        assertTrue(result.isEmpty());
    }
}
