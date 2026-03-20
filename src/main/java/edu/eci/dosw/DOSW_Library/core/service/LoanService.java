package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.exception.ResourceNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyContext;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyStrategy;
import edu.eci.dosw.DOSW_Library.core.util.DateUtil;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.core.validator.LoanValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class LoanService {
    private final Map<Long, Loan> loans = new ConcurrentHashMap<>();
    private final IdGeneratorUtil idGenerator = new IdGeneratorUtil();
    private final UserService userService;
    private final BookService bookService;
    private final LoanPolicyContext loanPolicyContext;

    public LoanService(UserService userService, BookService bookService, LoanPolicyContext loanPolicyContext) {
        this.userService = userService;
        this.bookService = bookService;
        this.loanPolicyContext = loanPolicyContext;
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans.values());
    }

    public Loan getLoanById(Long id) {
        Loan loan = loans.get(id);
        if (loan == null) {
            throw new ResourceNotFoundException("Loan not found with id: " + id);
        }
        return loan;
    }

    public Loan createLoan(Loan request) {
        LoanValidator.validateForCreate(request);

        User user = userService.getUserById(request.getUserId());
        bookService.getBookById(request.getBookId());

        LoanPolicyStrategy policy = loanPolicyContext.getPolicy(user.getUserType());

        long activeLoans = loans.values().stream()
                .filter(loan -> loan.getUserId().equals(request.getUserId()))
                .filter(loan -> !loan.isReturned())
                .count();

        if (activeLoans >= policy.maxConcurrentLoans()) {
            throw new LoanLimitExceededException("User exceeded loan limit for plan " + user.getUserType());
        }

        bookService.decrementAvailableCopies(request.getBookId());

        LocalDate start = DateUtil.today();
        Loan loan = new Loan();
        loan.setId(idGenerator.nextId());
        loan.setUserId(request.getUserId());
        loan.setBookId(request.getBookId());
        loan.setLoanDate(start);
        loan.setDueDate(DateUtil.addDays(start, policy.loanDays()));
        loan.setReturned(false);

        loans.put(loan.getId(), loan);
        return loan;
    }

    public Loan updateLoan(Long id, Loan update) {
        Loan existing = getLoanById(id);

        if (!existing.isReturned() && update.isReturned()) {
            return returnLoan(id);
        }

        if (existing.isReturned() && !update.isReturned()) {
            throw new IllegalStateException("Cannot reopen a returned loan");
        }

        if (update.getDueDate() != null) {
            existing.setDueDate(update.getDueDate());
        }

        loans.put(id, existing);
        return existing;
    }

    public Loan returnLoan(Long id) {
        Loan loan = getLoanById(id);
        if (loan.isReturned()) {
            throw new IllegalStateException("Loan is already returned");
        }

        loan.setReturned(true);
        loan.setReturnedDate(DateUtil.today());
        bookService.incrementAvailableCopies(loan.getBookId());
        loans.put(id, loan);
        return loan;
    }

    public void deleteLoan(Long id) {
        Loan loan = getLoanById(id);
        if (!loan.isReturned()) {
            bookService.incrementAvailableCopies(loan.getBookId());
        }
        loans.remove(id);
    }
}
