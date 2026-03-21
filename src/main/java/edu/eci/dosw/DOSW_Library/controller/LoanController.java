package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanRequestDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.LoanResponseDTO;
import edu.eci.dosw.DOSW_Library.controller.mapper.LoanMapper;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.LoanService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService loanService;
    private final LoanMapper loanMapper;

    public LoanController(LoanService loanService, LoanMapper loanMapper) {
        this.loanService = loanService;
        this.loanMapper = loanMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<LoanResponseDTO> getAllLoans() {
        return loanService.getAllLoans().stream().map(loanMapper::toResponse).toList();
    }

    @GetMapping("/my-loans")
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    public List<LoanResponseDTO> getMyLoans(@AuthenticationPrincipal User user) {
        return loanService.getLoansForUser(user.getId()).stream().map(loanMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public LoanResponseDTO getLoanById(@PathVariable Long id) {
        return loanMapper.toResponse(loanService.getLoanById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponseDTO createLoan(@AuthenticationPrincipal User user, @Valid @RequestBody LoanRequestDTO request) {
        Loan createdLoan = loanService.createLoan(user.getId(), request.getBookId());
        return loanMapper.toResponse(createdLoan);
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    public LoanResponseDTO returnLoan(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return loanMapper.toResponse(loanService.returnLoan(id, user.getId(), user.getRole()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
    }
}
