package edu.eci.dosw.DOSW_Library.core.strategy;

import edu.eci.dosw.DOSW_Library.core.model.UserType;

public interface LoanPolicyStrategy {
    UserType supportsUserType();

    int maxConcurrentLoans();

    int loanDays();
}

