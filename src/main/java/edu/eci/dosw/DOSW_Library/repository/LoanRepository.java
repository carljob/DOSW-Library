package edu.eci.dosw.DOSW_Library.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    List<Loan> findAll();

    Optional<Loan> findById(Long id);

    Loan save(Loan loan);

    void delete(Loan loan);

    List<Loan> findByUserId(Long userId);

    long countByUserIdAndReturnedFalse(Long userId);
}

