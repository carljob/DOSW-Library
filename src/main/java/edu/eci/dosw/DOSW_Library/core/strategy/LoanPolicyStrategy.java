package edu.eci.dosw.DOSW_Library.core.strategy;

import edu.eci.dosw.DOSW_Library.core.model.Role;

public interface LoanPolicyStrategy {
    Role supportsRole();

    int maxConcurrentLoans();

    int loanDays();
}
