package edu.eci.dosw.DOSW_Library.persistence.relational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.BookAvailability;
import edu.eci.dosw.DOSW_Library.core.model.BookMetadata;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.BookEntity;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.BookEntity.BookAvailabilityEmbeddable;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.BookEntity.BookMetadataEmbeddable;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class BookPersistenceMapper {

    public Book toDomain(BookEntity entity) {
        if (entity == null) return null;

        BookMetadata metadata = null;
        if (entity.getMetadata() != null) {
            BookMetadataEmbeddable m = entity.getMetadata();
            metadata = new BookMetadata(m.getPages(), m.getLanguage(), m.getPublisher());
        }

        BookAvailability availability = null;
        if (entity.getAvailability() != null) {
            BookAvailabilityEmbeddable a = entity.getAvailability();
            availability = new BookAvailability(
                    a.getStatus(), a.getTotalCopies(), a.getAvailableCopies(), a.getLoanedCopies());
        }

        return new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getIsbn(),
                entity.getTotalStock(),
                entity.getAvailableStock(),
                entity.getCategories() != null ? entity.getCategories() : new ArrayList<>(),
                entity.getPublicationType(),
                entity.getPublicationDate(),
                entity.getAddedDate(),
                metadata,
                availability
        );
    }

    public BookEntity toEntity(Book domain) {
        if (domain == null) return null;

        BookEntity entity = new BookEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setAuthor(domain.getAuthor());
        entity.setIsbn(domain.getIsbn());
        entity.setTotalStock(domain.getTotalStock());
        entity.setAvailableStock(domain.getAvailableStock());
        entity.setCategories(domain.getCategories() != null ? domain.getCategories() : new ArrayList<>());
        entity.setPublicationType(domain.getPublicationType());
        entity.setPublicationDate(domain.getPublicationDate());
        entity.setAddedDate(domain.getAddedDate());

        if (domain.getMetadata() != null) {
            BookMetadata m = domain.getMetadata();
            entity.setMetadata(new BookMetadataEmbeddable(m.getPages(), m.getLanguage(), m.getPublisher()));
        }

        if (domain.getAvailability() != null) {
            BookAvailability a = domain.getAvailability();
            entity.setAvailability(new BookAvailabilityEmbeddable(
                    a.getStatus(), a.getTotalCopies(), a.getAvailableCopies(), a.getLoanedCopies()));
        }

        return entity;
    }
}
