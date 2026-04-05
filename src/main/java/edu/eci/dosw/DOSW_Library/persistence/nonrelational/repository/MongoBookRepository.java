package edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository;

import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.BookDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoBookRepository extends MongoRepository<BookDocument, String> {
    Optional<BookDocument> findByIsbn(String isbn);
}

