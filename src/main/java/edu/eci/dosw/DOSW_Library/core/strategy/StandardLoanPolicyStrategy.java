package edu.eci.dosw.DOSW_Library.core.strategy;

import edu.eci.dosw.DOSW_Library.core.model.Role;
import org.springframework.stereotype.Component;

@Component
public class StandardLoanPolicyStrategy implements LoanPolicyStrategy {
    @Override
    public Role supportsRole() {
        return Role.USER;
    }

    @Override
    public int maxConcurrentLoans() {
        return 2;
    }

    @Override
    public int loanDays() {
        return 7;
    }
}
