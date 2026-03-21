package edu.eci.dosw.DOSW_Library.core.strategy;

import edu.eci.dosw.DOSW_Library.core.model.Role;
import org.springframework.stereotype.Component;

@Component
public class PremiumLoanPolicyStrategy implements LoanPolicyStrategy {
    @Override
    public Role supportsRole() {
        return Role.LIBRARIAN;
    }

    @Override
    public int maxConcurrentLoans() {
        return 5;
    }

    @Override
    public int loanDays() {
        return 14;
    }
}
