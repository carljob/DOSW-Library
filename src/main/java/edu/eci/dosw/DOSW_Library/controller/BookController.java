package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.BookRequestDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.BookResponseDTO;
import edu.eci.dosw.DOSW_Library.controller.mapper.BookMapper;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    public List<BookResponseDTO> getAllBooks() {
        return bookService.getAllBooks().stream().map(bookMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    public BookResponseDTO getBookById(@PathVariable Long id) {
        return bookMapper.toResponse(bookService.getBookById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDTO createBook(@Valid @RequestBody BookRequestDTO request) {
        Book createdBook = bookService.createBook(bookMapper.toEntity(request));
        return bookMapper.toResponse(createdBook);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public BookResponseDTO updateBook(@PathVariable Long id, @Valid @RequestBody BookRequestDTO request) {
        Book updatedBook = bookService.updateBook(id, bookMapper.toEntity(request));
        return bookMapper.toResponse(updatedBook);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
