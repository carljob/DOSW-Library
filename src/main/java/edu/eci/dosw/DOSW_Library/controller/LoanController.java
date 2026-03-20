package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.controller.mapper.LoanMapper;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.service.LoanService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<LoanDTO> getAllLoans() {
        return loanService.getAllLoans().stream().map(LoanMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public LoanDTO getLoanById(@PathVariable Long id) {
        return LoanMapper.toDto(loanService.getLoanById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDTO createLoan(@RequestBody LoanDTO loanDTO) {
        Loan createdLoan = loanService.createLoan(LoanMapper.toModel(loanDTO));
        return LoanMapper.toDto(createdLoan);
    }

    @PutMapping("/{id}")
    public LoanDTO updateLoan(@PathVariable Long id, @RequestBody LoanDTO loanDTO) {
        Loan updatedLoan = loanService.updateLoan(id, LoanMapper.toModel(loanDTO));
        return LoanMapper.toDto(updatedLoan);
    }

    @PutMapping("/{id}/return")
    public LoanDTO returnLoan(@PathVariable Long id) {
        return LoanMapper.toDto(loanService.returnLoan(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
    }
}
