package edu.eci.dosw.DOSW_Library.persistence.nonrelational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.BookAvailability;
import edu.eci.dosw.DOSW_Library.core.model.BookMetadata;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.BookDocument;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookDocumentMapper {

    public Book toDomain(BookDocument doc) {
        if (doc == null) {
            return null;
        }

        BookMetadata metadata = null;
        if (doc.getMetadata() != null) {
            metadata = new BookMetadata(
                    doc.getMetadata().getPaginas(),
                    doc.getMetadata().getIdioma(),
                    doc.getMetadata().getEmpresa()
            );
        }

        BookAvailability availability = null;
        if (doc.getAvailability() != null) {
            availability = new BookAvailability(
                    doc.getAvailability().getStatus(),
                    doc.getAvailability().getTotalCopias(),
                    doc.getAvailability().getCopiasDisponibles(),
                    doc.getAvailability().getCopiasPrestadas()
            );
        }

        return new Book(
                toLong(doc.getId()),
                doc.getTitulo(),
                doc.getAutor(),
                doc.getIsbn(),
                doc.getTotalStock(),
                doc.getAvailableStock(),
                doc.getCategorias() == null ? new ArrayList<>() : new ArrayList<>(doc.getCategorias()),
                doc.getTipoPublicacion(),
                doc.getFechaPublicacion(),
                doc.getFechaAgregado(),
                metadata,
                availability
        );
    }

    public BookDocument toDocument(Book book) {
        if (book == null) {
            return null;
        }

        BookDocument.BookMetadataDocument metadata = null;
        if (book.getMetadata() != null) {
            metadata = new BookDocument.BookMetadataDocument(
                    book.getMetadata().getPages(),
                    book.getMetadata().getLanguage(),
                    book.getMetadata().getPublisher()
            );
        }

        BookDocument.BookAvailabilityDocument availability = null;
        if (book.getAvailability() != null) {
            availability = new BookDocument.BookAvailabilityDocument(
                    book.getAvailability().getStatus(),
                    book.getAvailability().getTotalCopies(),
                    book.getAvailability().getAvailableCopies(),
                    book.getAvailability().getLoanedCopies()
            );
        }

        List<String> categorias = book.getCategories() == null ? new ArrayList<>() : new ArrayList<>(book.getCategories());

        return new BookDocument(
                toStringId(book.getId()),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                categorias,
                book.getPublicationType(),
                book.getPublicationDate(),
                book.getAddedDate(),
                book.getTotalStock(),
                book.getAvailableStock(),
                metadata,
                availability
        );
    }

    private Long toLong(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String toStringId(Long value) {
        return value == null ? null : String.valueOf(value);
    }
}

