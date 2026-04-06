package edu.eci.dosw.DOSW_Library.persistence.relational.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private int totalStock;

    @Column(nullable = false)
    private int availableStock;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_categories", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "category")
    private List<String> categories = new ArrayList<>();

    private String publicationType;

    private LocalDate publicationDate;

    private LocalDate addedDate;

    @Embedded
    private BookMetadataEmbeddable metadata;

    @Embedded
    private BookAvailabilityEmbeddable availability;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getTotalStock() { return totalStock; }
    public void setTotalStock(int totalStock) { this.totalStock = totalStock; }
    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }
    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
    public String getPublicationType() { return publicationType; }
    public void setPublicationType(String publicationType) { this.publicationType = publicationType; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }
    public LocalDate getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDate addedDate) { this.addedDate = addedDate; }
    public BookMetadataEmbeddable getMetadata() { return metadata; }
    public void setMetadata(BookMetadataEmbeddable metadata) { this.metadata = metadata; }
    public BookAvailabilityEmbeddable getAvailability() { return availability; }
    public void setAvailability(BookAvailabilityEmbeddable availability) { this.availability = availability; }

    @Embeddable
    public static class BookMetadataEmbeddable {
        @Column(name = "metadata_pages")
        private int pages;
        @Column(name = "metadata_language")
        private String language;
        @Column(name = "metadata_publisher")
        private String publisher;

        public BookMetadataEmbeddable() {}
        public BookMetadataEmbeddable(int pages, String language, String publisher) {
            this.pages = pages; this.language = language; this.publisher = publisher;
        }
        public int getPages() { return pages; }
        public void setPages(int pages) { this.pages = pages; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getPublisher() { return publisher; }
        public void setPublisher(String publisher) { this.publisher = publisher; }
    }

    @Embeddable
    public static class BookAvailabilityEmbeddable {
        @Column(name = "availability_status")
        private String status;
        @Column(name = "availability_total_copies")
        private int totalCopies;
        @Column(name = "availability_available_copies")
        private int availableCopies;
        @Column(name = "availability_loaned_copies")
        private int loanedCopies;

        public BookAvailabilityEmbeddable() {}
        public BookAvailabilityEmbeddable(String status, int totalCopies, int availableCopies, int loanedCopies) {
            this.status = status; this.totalCopies = totalCopies;
            this.availableCopies = availableCopies; this.loanedCopies = loanedCopies;
        }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getTotalCopies() { return totalCopies; }
        public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
        public int getAvailableCopies() { return availableCopies; }
        public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
        public int getLoanedCopies() { return loanedCopies; }
        public void setLoanedCopies(int loanedCopies) { this.loanedCopies = loanedCopies; }
    }
}
