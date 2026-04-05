package edu.eci.dosw.DOSW_Library.core.model;

public class BookAvailability {

    private String status;

    private int totalCopies;

    private int availableCopies;

    private int loanedCopies;

    public BookAvailability() {
    }

    public BookAvailability(String status, int totalCopies, int availableCopies, int loanedCopies) {
        this.status = status;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.loanedCopies = loanedCopies;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public int getLoanedCopies() {
        return loanedCopies;
    }

    public void setLoanedCopies(int loanedCopies) {
        this.loanedCopies = loanedCopies;
    }
}
