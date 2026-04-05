package edu.eci.dosw.DOSW_Library.core.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Book {

    private Long id;

    private String title;

    private String author;

    private String isbn;

    private int totalStock;

    private int availableStock;

    private List<String> categories;

    private String publicationType;

    private LocalDate publicationDate;

    private LocalDate addedDate;

    private BookMetadata metadata;

    private BookAvailability availability;

    public Book() {
        this.categories = new ArrayList<>();
    }

    public Book(Long id, String title, String author, String isbn, int totalStock, int availableStock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalStock = totalStock;
        this.availableStock = availableStock;
        this.categories = new ArrayList<>();
    }

    public Book(Long id, String title, String author, String isbn, int totalStock, int availableStock,
                List<String> categories, String publicationType, LocalDate publicationDate, LocalDate addedDate,
                BookMetadata metadata, BookAvailability availability) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalStock = totalStock;
        this.availableStock = availableStock;
        this.categories = categories;
        this.publicationType = publicationType;
        this.publicationDate = publicationDate;
        this.addedDate = addedDate;
        this.metadata = metadata;
        this.availability = availability;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(String publicationType) {
        this.publicationType = publicationType;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    public BookMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(BookMetadata metadata) {
        this.metadata = metadata;
    }

    public BookAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(BookAvailability availability) {
        this.availability = availability;
    }
}
