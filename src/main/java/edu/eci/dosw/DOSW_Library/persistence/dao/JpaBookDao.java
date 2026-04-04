package edu.eci.dosw.DOSW_Library.persistence.dao;

import edu.eci.dosw.DOSW_Library.persistence.entity.BookEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBookDao extends JpaRepository<BookEntity, Long> {
    Optional<BookEntity> findByIsbn(String isbn);
}

