package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.core.model.Loan;

public final class LoanMapper {
    private LoanMapper() {
    }

    public static Loan toModel(LoanDTO dto) {
        return new Loan(
                dto.getId(),
                dto.getUserId(),
                dto.getBookId(),
                dto.getLoanDate(),
                dto.getDueDate(),
                dto.getReturnedDate(),
                dto.isReturned()
        );
    }

    public static LoanDTO toDto(Loan loan) {
        return new LoanDTO(
                loan.getId(),
                loan.getUserId(),
                loan.getBookId(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnedDate(),
                loan.isReturned()
        );
    }
}
