package edu.eci.dosw.DOSW_Library.core.strategy;

import edu.eci.dosw.DOSW_Library.core.model.Role;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LoanPolicyContext {
    private final Map<Role, LoanPolicyStrategy> strategies;

    public LoanPolicyContext(List<LoanPolicyStrategy> strategyList) {
        this.strategies = new EnumMap<>(Role.class);
        for (LoanPolicyStrategy strategy : strategyList) {
            this.strategies.put(strategy.supportsRole(), strategy);
        }
    }

    public LoanPolicyStrategy getPolicy(Role role) {
        LoanPolicyStrategy strategy = strategies.get(role);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy configured for role: " + role);
        }
        return strategy;
    }
}
