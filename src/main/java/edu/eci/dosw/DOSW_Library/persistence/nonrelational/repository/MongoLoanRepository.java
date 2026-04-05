package edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository;

import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.LoanDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoLoanRepository extends MongoRepository<LoanDocument, String> {
    List<LoanDocument> findByUsuarioId(String usuarioId);

    long countByUsuarioIdAndDevueltoFalse(String usuarioId);
}

