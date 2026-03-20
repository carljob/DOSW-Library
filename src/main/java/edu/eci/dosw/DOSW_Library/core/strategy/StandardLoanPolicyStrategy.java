package edu.eci.dosw.DOSW_Library.core.strategy;

import edu.eci.dosw.DOSW_Library.core.model.UserType;
import org.springframework.stereotype.Component;

@Component
public class StandardLoanPolicyStrategy implements LoanPolicyStrategy {
    @Override
    public UserType supportsUserType() {
        return UserType.STANDARD;
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

