package edu.eci.dosw.DOSW_Library.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.eci.dosw.DOSW_Library.core.exception.UnauthorizedOperationException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.Role;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyContext;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyStrategy;
import edu.eci.dosw.DOSW_Library.repository.LoanRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private LoanPolicyContext loanPolicyContext;

    @Mock
    private LoanPolicyStrategy loanPolicyStrategy;

    @InjectMocks
    private LoanService loanService;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Laura", "laura@email.com", "laura", "encoded", Role.USER);
        book = new Book(10L, "Clean Code", "Robert Martin", "ISBN-1", 3, 2);
    }

    @Test
    void shouldCreateLoanAndDecrementStock() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(bookService.getBookById(10L)).thenReturn(book);
        when(loanPolicyContext.getPolicy(Role.USER)).thenReturn(loanPolicyStrategy);
        when(loanPolicyStrategy.maxConcurrentLoans()).thenReturn(2);
        when(loanPolicyStrategy.loanDays()).thenReturn(7);
        when(loanRepository.countByUserIdAndReturnedFalse(1L)).thenReturn(0L);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan created = loanService.createLoan(1L, 10L);

        assertEquals(user, created.getUser());
        assertEquals(book, created.getBook());
        assertFalse(created.isReturned());
        verify(bookService).decrementAvailableCopies(10L);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void shouldReturnLoanAndIncrementStock() {
        Loan loan = new Loan(100L, book, user, LocalDate.now(), null, false);
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan returned = loanService.returnLoan(100L, 1L, Role.USER);

        assertTrue(returned.isReturned());
        assertEquals(LocalDate.now(), returned.getReturnDate());
        verify(bookService).incrementAvailableCopies(10L);
        verify(loanRepository).save(loan);
    }

    @Test
    void shouldDenyReturnWhenUserIsNotOwnerOrLibrarian() {
        User otherUser = new User(2L, "Ana", "ana@email.com", "ana", "encoded", Role.USER);
        Loan loan = new Loan(100L, book, otherUser, LocalDate.now(), null, false);
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));

        assertThrows(UnauthorizedOperationException.class, () -> loanService.returnLoan(100L, 1L, Role.USER));
        verify(bookService, never()).incrementAvailableCopies(any(Long.class));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void librarianCanReturnAnyLoan() {
        User otherUser = new User(2L, "Ana", "ana@email.com", "ana", "encoded", Role.USER);
        Loan loan = new Loan(100L, book, otherUser, LocalDate.now(), null, false);
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan returned = loanService.returnLoan(100L, 99L, Role.LIBRARIAN);

        assertTrue(returned.isReturned());
        verify(bookService).incrementAvailableCopies(10L);
    }
}
