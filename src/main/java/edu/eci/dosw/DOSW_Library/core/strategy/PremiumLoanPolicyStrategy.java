package edu.eci.dosw.DOSW_Library.core.strategy;

import edu.eci.dosw.DOSW_Library.core.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PremiumLoanPolicyStrategy implements LoanPolicyStrategy {
    private final int maxConcurrentLoans;
    private final int loanDays;

    public PremiumLoanPolicyStrategy(
            @Value("${loan.policy.librarian.max-concurrent:5}") int maxConcurrentLoans,
            @Value("${loan.policy.librarian.days:14}") int loanDays
    ) {
        this.maxConcurrentLoans = maxConcurrentLoans;
        this.loanDays = loanDays;
    }

    @Override
    public Role supportsRole() {
        return Role.LIBRARIAN;
    }

    @Override
    public int maxConcurrentLoans() {
        return maxConcurrentLoans;
    }

    @Override
    public int loanDays() {
        return loanDays;
    }
}
