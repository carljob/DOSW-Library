package edu.eci.dosw.DOSW_Library.persistence.relational.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "loan_history")
public class LoanHistoryEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status;

    @Column(name = "executed_at")
    private LocalDate executedAt;

    public LoanHistoryEntryEntity() {}

    public LoanHistoryEntryEntity(String status, LocalDate executedAt) {
        this.status = status;
        this.executedAt = executedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDate executedAt) { this.executedAt = executedAt; }
}
