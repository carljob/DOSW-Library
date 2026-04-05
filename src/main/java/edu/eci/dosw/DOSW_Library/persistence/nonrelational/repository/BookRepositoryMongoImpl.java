package edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.BookDocument;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.mapper.BookDocumentMapper;
import edu.eci.dosw.DOSW_Library.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mongo")
public class BookRepositoryMongoImpl implements BookRepository {

    private final MongoBookRepository mongoBookRepository;
    private final BookDocumentMapper mapper;

    public BookRepositoryMongoImpl(MongoBookRepository mongoBookRepository, BookDocumentMapper mapper) {
        this.mongoBookRepository = mongoBookRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Book> findAll() {
        return mongoBookRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return mongoBookRepository.findById(String.valueOf(id)).map(mapper::toDomain);
    }

    @Override
    public Book save(Book book) {
        BookDocument document = mapper.toDocument(book);
        if (document.getId() == null) {
            document.setId(String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits())));
        }
        return mapper.toDomain(mongoBookRepository.save(document));
    }

    @Override
    public void delete(Book book) {
        if (book != null && book.getId() != null) {
            mongoBookRepository.deleteById(String.valueOf(book.getId()));
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return mongoBookRepository.findByIsbn(isbn).map(mapper::toDomain);
    }
}

