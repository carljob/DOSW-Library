package edu.eci.dosw.DOSW_Library.persistence.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.dao.JpaBookDao;
import edu.eci.dosw.DOSW_Library.persistence.mapper.BookPersistenceMapper;
import edu.eci.dosw.DOSW_Library.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepositoryImpl implements BookRepository {

    private final JpaBookDao jpaBookDao;
    private final BookPersistenceMapper mapper;

    public BookRepositoryImpl(JpaBookDao jpaBookDao, BookPersistenceMapper mapper) {
        this.jpaBookDao = jpaBookDao;
        this.mapper = mapper;
    }

    @Override
    public List<Book> findAll() {
        return jpaBookDao.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return jpaBookDao.findById(id).map(mapper::toDomain);
    }

    @Override
    public Book save(Book book) {
        return mapper.toDomain(jpaBookDao.save(mapper.toEntity(book)));
    }

    @Override
    public void delete(Book book) {
        jpaBookDao.delete(mapper.toEntity(book));
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return jpaBookDao.findByIsbn(isbn).map(mapper::toDomain);
    }
}

