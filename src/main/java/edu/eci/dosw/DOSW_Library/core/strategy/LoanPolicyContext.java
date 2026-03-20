package edu.eci.dosw.DOSW_Library.core.strategy;

import edu.eci.dosw.DOSW_Library.core.model.UserType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LoanPolicyContext {
    private final Map<UserType, LoanPolicyStrategy> strategies;

    public LoanPolicyContext(List<LoanPolicyStrategy> strategyList) {
        this.strategies = new EnumMap<>(UserType.class);
        for (LoanPolicyStrategy strategy : strategyList) {
            this.strategies.put(strategy.supportsUserType(), strategy);
        }
    }

    public LoanPolicyStrategy getPolicy(UserType userType) {
        LoanPolicyStrategy strategy = strategies.get(userType);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy configured for user type: " + userType);
        }
        return strategy;
    }
}

