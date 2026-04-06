package edu.eci.dosw.DOSW_Library.persistence.relational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanHistoryEntry;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanHistoryEntryEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LoanPersistenceMapper {

    private final BookPersistenceMapper bookMapper;
    private final UserPersistenceMapper userMapper;

    public LoanPersistenceMapper(BookPersistenceMapper bookMapper, UserPersistenceMapper userMapper) {
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
    }

    public Loan toDomain(LoanEntity entity) {
        if (entity == null) return null;

        List<LoanHistoryEntry> history = new ArrayList<>();
        if (entity.getHistory() != null) {
            entity.getHistory().stream()
                    .map(h -> new LoanHistoryEntry(h.getStatus(), h.getExecutedAt()))
                    .forEach(history::add);
        }

        return new Loan(
                entity.getId(),
                bookMapper.toDomain(entity.getBook()),
                userMapper.toDomain(entity.getUser()),
                entity.getLoanDate(),
                entity.getReturnDate(),
                entity.isReturned(),
                history
        );
    }

    public LoanEntity toEntity(Loan domain) {
        if (domain == null) return null;

        LoanEntity entity = new LoanEntity();
        entity.setId(domain.getId());
        entity.setLoanDate(domain.getLoanDate());
        entity.setReturnDate(domain.getReturnDate());
        entity.setReturned(domain.isReturned());

        if (domain.getHistory() != null) {
            List<LoanHistoryEntryEntity> historyEntities = domain.getHistory().stream()
                    .map(h -> new LoanHistoryEntryEntity(h.getStatus(), h.getExecutedAt()))
                    .collect(java.util.stream.Collectors.toList());
            entity.setHistory(historyEntities);
        }

        return entity;
    }
}
