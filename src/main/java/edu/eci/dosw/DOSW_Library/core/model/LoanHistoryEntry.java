package edu.eci.dosw.DOSW_Library.core.model;

import java.time.LocalDate;

public class LoanHistoryEntry {

    private String status;

    private LocalDate executedAt;

    public LoanHistoryEntry() {
    }

    public LoanHistoryEntry(String status, LocalDate executedAt) {
        this.status = status;
        this.executedAt = executedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDate executedAt) {
        this.executedAt = executedAt;
    }
}
