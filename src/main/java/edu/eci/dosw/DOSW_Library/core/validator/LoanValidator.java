package edu.eci.dosw.DOSW_Library.core.validator;

import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;

public final class LoanValidator {
    private LoanValidator() {
    }

    public static void validateForCreate(Long bookId) {
        ValidationUtil.requireNotNull(bookId, "Loan bookId is required");
    }
}
