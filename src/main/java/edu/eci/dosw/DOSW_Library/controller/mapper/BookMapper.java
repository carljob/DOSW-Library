package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.core.model.Book;

public final class BookMapper {
    private BookMapper() {
    }

    public static Book toModel(BookDTO dto) {
        return new Book(
                dto.getId(),
                dto.getTitle(),
                dto.getAuthor(),
                dto.getIsbn(),
                dto.getTotalCopies(),
                dto.getAvailableCopies()
        );
    }

    public static BookDTO toDto(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }
}
