package edu.eci.dosw.DOSW_Library.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);

    long countByUserIdAndReturnedFalse(Long userId);
}

