package edu.eci.dosw.DOSW_Library.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
}

