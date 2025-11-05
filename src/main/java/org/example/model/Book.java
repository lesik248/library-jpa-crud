package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "book")
@NamedQueries({
        @NamedQuery(
                name = "Book.findById",
                query = "SELECT b FROM Book b WHERE b.id = :id"
        ),
        @NamedQuery(
                name = "Book.findAll",
                query = "SELECT b FROM Book b"
        )
})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String author;
    private int copies;

    public Book() {}

    public Book(int id, String title, String author, int copies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.copies = copies;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
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
    public int getCopies() {
        return copies;
    }
    public void setCopies(int copies) {
        this.copies = copies;
    }
}


