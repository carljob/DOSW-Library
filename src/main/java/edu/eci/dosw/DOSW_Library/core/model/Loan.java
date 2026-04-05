package edu.eci.dosw.DOSW_Library.core.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Loan {

    private Long id;

    private Book book;

    private User user;

    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;

    private List<LoanHistoryEntry> history;

    public Loan() {
        this.history = new ArrayList<>();
    }

    public Loan(Long id, Book book, User user, LocalDate loanDate, LocalDate returnDate, boolean returned) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.returned = returned;
        this.history = new ArrayList<>();
    }

    public Loan(Long id, Book book, User user, LocalDate loanDate, LocalDate returnDate, boolean returned,
                List<LoanHistoryEntry> history) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.returned = returned;
        this.history = history;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public List<LoanHistoryEntry> getHistory() {
        return history;
    }

    public void setHistory(List<LoanHistoryEntry> history) {
        this.history = history;
    }
}
