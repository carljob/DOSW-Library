package edu.eci.dosw.DOSW_Library.persistence.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookPersistenceMapper {

    public Book toDomain(BookEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getIsbn(),
                entity.getTotalStock(),
                entity.getAvailableStock()
        );
    }

    public BookEntity toEntity(Book domain) {
        if (domain == null) {
            return null;
        }
        BookEntity entity = new BookEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setAuthor(domain.getAuthor());
        entity.setIsbn(domain.getIsbn());
        entity.setTotalStock(domain.getTotalStock());
        entity.setAvailableStock(domain.getAvailableStock());
        return entity;
    }
}

