package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.exception.ResourceNotFoundException;
import edu.eci.dosw.DOSW_Library.core.exception.UnauthorizedOperationException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.Role;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyContext;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyStrategy;
import edu.eci.dosw.DOSW_Library.core.util.DateUtil;
import edu.eci.dosw.DOSW_Library.core.validator.LoanValidator;
import edu.eci.dosw.DOSW_Library.repository.LoanRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserService userService;
    private final BookService bookService;
    private final LoanPolicyContext loanPolicyContext;

    public LoanService(LoanRepository loanRepository, UserService userService, BookService bookService, LoanPolicyContext loanPolicyContext) {
        this.loanRepository = loanRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.loanPolicyContext = loanPolicyContext;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
    }

    @Transactional
    public Loan createLoan(Long userId, Long bookId) {
        LoanValidator.validateForCreate(bookId);

        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        if (book.getAvailableStock() <= 0) {
            throw new BookNotAvailableException("Book has no available stock: " + bookId);
        }

        LoanPolicyStrategy policy = loanPolicyContext.getPolicy(user.getRole());
        long activeLoans = loanRepository.countByUserIdAndReturnedFalse(userId);
        if (activeLoans >= policy.maxConcurrentLoans()) {
            throw new LoanLimitExceededException("User exceeded active loan limit");
        }

        bookService.decrementAvailableCopies(bookId);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(DateUtil.today());
        loan.setReturnDate(DateUtil.addDays(DateUtil.today(), policy.loanDays()));
        loan.setReturned(false);
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan updateLoan(Long id, Loan update) {
        Loan existing = getLoanById(id);
        if (update.isReturned() && !existing.isReturned()) {
            return returnLoan(id, update.getUser().getId(), update.getUser().getRole());
        }
        return existing;
    }

    @Transactional
    public Loan returnLoan(Long loanId, Long requesterUserId, Role requesterRole) {
        Loan loan = getLoanById(loanId);

        boolean isOwner = loan.getUser().getId().equals(requesterUserId);
        boolean isLibrarian = requesterRole == Role.LIBRARIAN;
        if (!isOwner && !isLibrarian) {
            throw new UnauthorizedOperationException("You cannot return loans from other users");
        }

        if (loan.isReturned()) {
            throw new IllegalStateException("Loan is already returned");
        }

        loan.setReturned(true);
        loan.setReturnDate(DateUtil.today());
        bookService.incrementAvailableCopies(loan.getBook().getId());
        return loanRepository.save(loan);
    }

    @Transactional
    public void deleteLoan(Long id) {
        Loan loan = getLoanById(id);
        if (!loan.isReturned()) {
            bookService.incrementAvailableCopies(loan.getBook().getId());
        }
        loanRepository.delete(loan);
    }

    public List<Loan> getLoansForUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }
}
