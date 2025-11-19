package org.example.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "logbook")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "book_id")
    private int bookId;
    @Column(name = "reader_id")
    private int readerId;
    @Column(name = "issue_date")
    private String issueDate;
    @Column(name = "return_date", insertable = false, updatable = false)
    private LocalDate returnDate;
    private int debt;

    public Log() {}
    public Log(int id, int bookId, int readerId, String issueDate, LocalDate returnDate, int debt) {
        this.id = id;
        this.bookId = bookId;
        this.readerId = readerId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.debt = debt;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getBookId() {
        return bookId;
    }
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    public int getReaderId() {
        return readerId;
    }
    public void setReaderId(int readerId) {
        this.readerId = readerId;
    }
    public String getIssueDate() {
        return issueDate;
    }
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
    public LocalDate getReturnDate() {
        return returnDate;
    }
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    public int getDebt() {
        return debt;
    }
    public void setDebt(int debt) {
        this.debt = debt;
    }
}
