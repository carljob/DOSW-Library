package edu.eci.dosw.DOSW_Library.core.model;

public class BookMetadata {

    private int pages;

    private String language;

    private String publisher;

    public BookMetadata() {
    }

    public BookMetadata(int pages, String language, String publisher) {
        this.pages = pages;
        this.language = language;
        this.publisher = publisher;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}

