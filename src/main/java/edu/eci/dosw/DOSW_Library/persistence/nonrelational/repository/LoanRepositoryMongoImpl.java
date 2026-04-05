package edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.LoanDocument;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.mapper.LoanDocumentMapper;
import edu.eci.dosw.DOSW_Library.repository.LoanRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mongo")
public class LoanRepositoryMongoImpl implements LoanRepository {

    private final MongoLoanRepository mongoLoanRepository;
    private final LoanDocumentMapper mapper;

    public LoanRepositoryMongoImpl(MongoLoanRepository mongoLoanRepository, LoanDocumentMapper mapper) {
        this.mongoLoanRepository = mongoLoanRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Loan> findAll() {
        return mongoLoanRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return mongoLoanRepository.findById(String.valueOf(id)).map(mapper::toDomain);
    }

    @Override
    public Loan save(Loan loan) {
        LoanDocument document = mapper.toDocument(loan);
        if (document.getId() == null) {
            document.setId(String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits())));
        }
        return mapper.toDomain(mongoLoanRepository.save(document));
    }

    @Override
    public void delete(Loan loan) {
        if (loan != null && loan.getId() != null) {
            mongoLoanRepository.deleteById(String.valueOf(loan.getId()));
        }
    }

    @Override
    public List<Loan> findByUserId(Long userId) {
        return mongoLoanRepository.findByUsuarioId(String.valueOf(userId)).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByUserIdAndReturnedFalse(Long userId) {
        return mongoLoanRepository.countByUsuarioIdAndDevueltoFalse(String.valueOf(userId));
    }
}

