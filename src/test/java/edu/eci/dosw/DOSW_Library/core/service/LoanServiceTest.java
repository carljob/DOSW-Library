package edu.eci.dosw.DOSW_Library.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.eci.dosw.DOSW_Library.core.exception.LoanLimitExceededException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.model.UserType;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyContext;
import edu.eci.dosw.DOSW_Library.core.strategy.PremiumLoanPolicyStrategy;
import edu.eci.dosw.DOSW_Library.core.strategy.StandardLoanPolicyStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoanServiceTest {

    @Test
    void shouldApplyStandardLoanLimit() {
        UserService userService = new UserService();
        BookService bookService = new BookService();
        LoanPolicyContext context = new LoanPolicyContext(List.of(new StandardLoanPolicyStrategy(), new PremiumLoanPolicyStrategy()));
        LoanService loanService = new LoanService(userService, bookService, context);

        User user = userService.createUser(new User(null, "Laura", "laura@email.com", UserType.STANDARD));
        Book book1 = bookService.createBook(new Book(null, "Book1", "Author", "ISBN-1", 3, 3));
        Book book2 = bookService.createBook(new Book(null, "Book2", "Author", "ISBN-2", 3, 3));
        Book book3 = bookService.createBook(new Book(null, "Book3", "Author", "ISBN-3", 3, 3));

        loanService.createLoan(new Loan(null, user.getId(), book1.getId(), null, null, null, false));
        loanService.createLoan(new Loan(null, user.getId(), book2.getId(), null, null, null, false));

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.createLoan(new Loan(null, user.getId(), book3.getId(), null, null, null, false)));
    }

    @Test
    void shouldReturnLoanAndIncreaseInventory() {
        UserService userService = new UserService();
        BookService bookService = new BookService();
        LoanPolicyContext context = new LoanPolicyContext(List.of(new StandardLoanPolicyStrategy(), new PremiumLoanPolicyStrategy()));
        LoanService loanService = new LoanService(userService, bookService, context);

        User user = userService.createUser(new User(null, "Pepe", "pepe@email.com", UserType.PREMIUM));
        Book book = bookService.createBook(new Book(null, "DDD", "Evans", "ISBN-4", 1, 1));

        Loan loan = loanService.createLoan(new Loan(null, user.getId(), book.getId(), null, null, null, false));
        assertEquals(0, bookService.getBookById(book.getId()).getAvailableCopies());

        loanService.returnLoan(loan.getId());
        assertEquals(1, bookService.getBookById(book.getId()).getAvailableCopies());
    }
}

