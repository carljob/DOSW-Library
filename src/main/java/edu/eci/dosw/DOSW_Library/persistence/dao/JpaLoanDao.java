package edu.eci.dosw.DOSW_Library.persistence.dao;

import edu.eci.dosw.DOSW_Library.persistence.entity.LoanEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLoanDao extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findByUserId(Long userId);

    long countByUserIdAndReturnedFalse(Long userId);
}

