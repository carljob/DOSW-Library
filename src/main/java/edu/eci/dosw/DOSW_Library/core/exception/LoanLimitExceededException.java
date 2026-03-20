package edu.eci.dosw.DOSW_Library.core.exception;

public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
