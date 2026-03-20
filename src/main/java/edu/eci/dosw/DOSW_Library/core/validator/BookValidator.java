package edu.eci.dosw.DOSW_Library.core.validator;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;

public final class BookValidator {
    private BookValidator() {
    }

    public static void validate(Book book) {
        ValidationUtil.requireNotNull(book, "Book cannot be null");
        ValidationUtil.requireNotBlank(book.getTitle(), "Book title is required");
        ValidationUtil.requireNotBlank(book.getAuthor(), "Book author is required");
        ValidationUtil.requireNotBlank(book.getIsbn(), "Book ISBN is required");
        ValidationUtil.requirePositive(book.getTotalCopies(), "Total copies must be greater than zero");
        ValidationUtil.requireZeroOrPositive(book.getAvailableCopies(), "Available copies cannot be negative");

        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new IllegalArgumentException("Available copies cannot exceed total copies");
        }
    }
}
