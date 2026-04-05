package edu.eci.dosw.DOSW_Library.persistence.relational.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.persistence.relational.dao.JpaBookDao;
import edu.eci.dosw.DOSW_Library.persistence.relational.dao.JpaLoanDao;
import edu.eci.dosw.DOSW_Library.persistence.relational.dao.JpaUserDao;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.DOSW_Library.persistence.relational.mapper.LoanPersistenceMapper;
import edu.eci.dosw.DOSW_Library.repository.LoanRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("relational")
public class LoanRepositoryJpaImpl implements LoanRepository {

    private final JpaLoanDao jpaLoanDao;
    private final JpaBookDao jpaBookDao;
    private final JpaUserDao jpaUserDao;
    private final LoanPersistenceMapper mapper;

    public LoanRepositoryJpaImpl(
            JpaLoanDao jpaLoanDao,
            JpaBookDao jpaBookDao,
            JpaUserDao jpaUserDao,
            LoanPersistenceMapper mapper
    ) {
        this.jpaLoanDao = jpaLoanDao;
        this.jpaBookDao = jpaBookDao;
        this.jpaUserDao = jpaUserDao;
        this.mapper = mapper;
    }

    @Override
    public List<Loan> findAll() {
        return jpaLoanDao.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return jpaLoanDao.findById(id).map(mapper::toDomain);
    }

    @Override
    public Loan save(Loan loan) {
        LoanEntity entity = new LoanEntity();
        entity.setId(loan.getId());
        entity.setLoanDate(loan.getLoanDate());
        entity.setReturnDate(loan.getReturnDate());
        entity.setReturned(loan.isReturned());

        if (loan.getBook() != null && loan.getBook().getId() != null) {
            entity.setBook(jpaBookDao.getReferenceById(loan.getBook().getId()));
        }
        if (loan.getUser() != null && loan.getUser().getId() != null) {
            entity.setUser(jpaUserDao.getReferenceById(loan.getUser().getId()));
        }

        return mapper.toDomain(jpaLoanDao.save(entity));
    }

    @Override
    public void delete(Loan loan) {
        if (loan.getId() != null) {
            jpaLoanDao.deleteById(loan.getId());
        }
    }

    @Override
    public List<Loan> findByUserId(Long userId) {
        return jpaLoanDao.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByUserIdAndReturnedFalse(Long userId) {
        return jpaLoanDao.countByUserIdAndReturnedFalse(userId);
    }
}


