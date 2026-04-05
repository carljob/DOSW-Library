package edu.eci.dosw.DOSW_Library.persistence.relational.dao;

import edu.eci.dosw.DOSW_Library.persistence.relational.entity.BookEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBookDao extends JpaRepository<BookEntity, Long> {
    Optional<BookEntity> findByIsbn(String isbn);
}


