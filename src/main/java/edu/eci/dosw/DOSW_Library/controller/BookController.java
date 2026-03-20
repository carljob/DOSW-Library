package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.controller.mapper.BookMapper;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks().stream().map(BookMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable Long id) {
        return BookMapper.toDto(bookService.getBookById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {
        Book createdBook = bookService.createBook(BookMapper.toModel(bookDTO));
        return BookMapper.toDto(createdBook);
    }

    @PutMapping("/{id}")
    public BookDTO updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        Book updatedBook = bookService.updateBook(id, BookMapper.toModel(bookDTO));
        return BookMapper.toDto(updatedBook);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
