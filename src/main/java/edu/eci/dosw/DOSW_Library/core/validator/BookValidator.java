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
        ValidationUtil.requirePositive(book.getTotalStock(), "Total stock must be greater than zero");
        ValidationUtil.requireZeroOrPositive(book.getAvailableStock(), "Available stock cannot be negative");

        if (book.getAvailableStock() > book.getTotalStock()) {
            throw new IllegalArgumentException("Available stock cannot exceed total stock");
        }
    }
}
