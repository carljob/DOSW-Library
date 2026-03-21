package edu.eci.dosw.DOSW_Library.controller.dto;

import jakarta.validation.constraints.NotNull;

public class LoanRequestDTO {
    @NotNull
    private Long bookId;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}

