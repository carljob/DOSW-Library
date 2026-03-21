package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.exception.BookNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.validator.BookValidator;
import edu.eci.dosw.DOSW_Library.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    @Transactional
    public Book createBook(Book book) {
        if (book.getAvailableStock() == 0) {
            book.setAvailableStock(book.getTotalStock());
        }
        BookValidator.validate(book);
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        Book existing = getBookById(id);
        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setIsbn(updatedBook.getIsbn());
        existing.setTotalStock(updatedBook.getTotalStock());

        if (updatedBook.getAvailableStock() > updatedBook.getTotalStock()) {
            existing.setAvailableStock(updatedBook.getTotalStock());
        } else {
            existing.setAvailableStock(updatedBook.getAvailableStock());
        }

        BookValidator.validate(existing);
        return bookRepository.save(existing);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book existing = getBookById(id);
        bookRepository.delete(existing);
    }

    @Transactional
    public void decrementAvailableCopies(Long id) {
        Book book = getBookById(id);
        if (book.getAvailableStock() <= 0) {
            throw new BookNotAvailableException("Book has no available stock: " + id);
        }
        book.setAvailableStock(book.getAvailableStock() - 1);
        bookRepository.save(book);
    }

    @Transactional
    public void incrementAvailableCopies(Long id) {
        Book book = getBookById(id);
        if (book.getAvailableStock() < book.getTotalStock()) {
            book.setAvailableStock(book.getAvailableStock() + 1);
        }
        bookRepository.save(book);
    }
}
