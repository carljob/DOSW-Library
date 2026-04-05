package edu.eci.dosw.DOSW_Library.persistence.relational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanEntity;
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
        if (entity == null) {
            return null;
        }
        return new Loan(
                entity.getId(),
                bookMapper.toDomain(entity.getBook()),
                userMapper.toDomain(entity.getUser()),
                entity.getLoanDate(),
                entity.getReturnDate(),
                entity.isReturned()
        );
    }
}


