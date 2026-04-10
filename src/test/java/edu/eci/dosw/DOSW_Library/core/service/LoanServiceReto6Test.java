package edu.eci.dosw.DOSW_Library.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.Role;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyContext;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyStrategy;
import edu.eci.dosw.DOSW_Library.repository.LoanRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanServiceReto6Test {

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
    private Loan loan;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Carlos", "carlos@email.com", "carlos0869", "encoded", Role.USER);
        book = new Book(10L, "Clean Code", "Robert Martin", "ISBN-1", 5, 4);
        loan = new Loan(100L, book, user, LocalDate.now(), LocalDate.now().plusDays(14), false);
    }

    // -----------------------------------------------------------------------
    // Test 1: Dado que tengo 1 reserva registrada, Cuando lo consulto a nivel
    // de servicio, entonces la consulta será exitosa validando el campo id.
    // -----------------------------------------------------------------------
    @Test
    void dadoQueHayUnaReserva_cuandoConsulto_entoncesEsExitosaValidandoId() {
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));

        Loan resultado = loanService.getLoanById(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
    }

    // -----------------------------------------------------------------------
    // Test 2: Dado que no hay ninguna reserva registrada, Cuando la consulto
    // a nivel de servicio, Entonces la consulta no retorna ningún resultado.
    // -----------------------------------------------------------------------
    @Test
    void dadoQueNoHayReservas_cuandoConsultoTodas_entoncesRetornaListaVacia() {
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        List<Loan> resultado = loanService.getAllLoans();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // -----------------------------------------------------------------------
    // Test 3: Dado que no hay ninguna reserva registrada, Cuando lo creo a
    // nivel de servicio, entonces la creación será exitosa.
    // -----------------------------------------------------------------------
    @Test
    void dadoQueNoHayReservas_cuandoCreo_entoncesLaCreacionEsExitosa() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(bookService.getBookById(10L)).thenReturn(book);
        when(loanPolicyContext.getPolicy(Role.USER)).thenReturn(loanPolicyStrategy);
        when(loanPolicyStrategy.maxConcurrentLoans()).thenReturn(3);
        when(loanPolicyStrategy.loanDays()).thenReturn(14);
        when(loanRepository.countByUserIdAndReturnedFalse(1L)).thenReturn(0L);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l = new Loan(200L, l.getBook(), l.getUser(), l.getLoanDate(), l.getReturnDate(), l.isReturned());
            return l;
        });

        Loan resultado = loanService.createLoan(1L, 10L);

        assertNotNull(resultado);
        assertEquals(book, resultado.getBook());
        assertEquals(user, resultado.getUser());
        assertFalse(resultado.isReturned());
        verify(loanRepository).save(any(Loan.class));
    }

    // -----------------------------------------------------------------------
    // Test 4: Dado que tengo 1 reserva registrada, Cuando la elimino a nivel
    // de servicio, entonces la eliminación será exitosa.
    // -----------------------------------------------------------------------
    @Test
    void dadoQueHayUnaReserva_cuandoElimino_entoncesLaEliminacionEsExitosa() {
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));
        doNothing().when(loanRepository).delete(loan);

        loanService.deleteLoan(100L);

        verify(loanRepository).delete(loan);
    }

    // -----------------------------------------------------------------------
    // Test 5: Dado que tengo 1 reserva registrada, Cuando la elimino y
    // consulto a nivel de servicio, entonces el resultado no retorna ningún resultado.
    // -----------------------------------------------------------------------
    @Test
    void dadoQueHayUnaReserva_cuandoEliminoYConsulto_entoncesNoHayResultados() {
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));
        doNothing().when(loanRepository).delete(loan);
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        loanService.deleteLoan(100L);
        List<Loan> resultado = loanService.getAllLoans();

        verify(loanRepository).delete(loan);
        assertTrue(resultado.isEmpty());
    }
}