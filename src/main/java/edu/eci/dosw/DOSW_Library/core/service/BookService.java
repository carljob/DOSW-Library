package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvaliableException;
import edu.eci.dosw.DOSW_Library.core.exception.ResourceNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.core.validator.BookValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final IdGeneratorUtil idGenerator = new IdGeneratorUtil();

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public Book getBookById(Long id) {
        Book book = books.get(id);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        return book;
    }

    public Book createBook(Book book) {
        if (book.getAvailableCopies() == 0) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        BookValidator.validate(book);
        Long id = idGenerator.nextId();
        book.setId(id);
        books.put(id, book);
        return book;
    }

    public Book updateBook(Long id, Book updatedBook) {
        BookValidator.validate(updatedBook);
        getBookById(id);
        updatedBook.setId(id);
        books.put(id, updatedBook);
        return updatedBook;
    }

    public void deleteBook(Long id) {
        Book removed = books.remove(id);
        if (removed == null) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
    }

    public void decrementAvailableCopies(Long id) {
        Book book = getBookById(id);
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvaliableException("Book has no available copies: " + id);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
    }

    public void incrementAvailableCopies(Long id) {
        Book book = getBookById(id);
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
        }
    }
}
