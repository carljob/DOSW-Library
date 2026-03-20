package edu.eci.dosw.DOSW_Library.core.validator;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;

public final class LoanValidator {
    private LoanValidator() {
    }

    public static void validateForCreate(Loan loan) {
        ValidationUtil.requireNotNull(loan, "Loan cannot be null");
        ValidationUtil.requireNotNull(loan.getUserId(), "Loan userId is required");
        ValidationUtil.requireNotNull(loan.getBookId(), "Loan bookId is required");
    }
}
