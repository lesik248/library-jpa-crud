package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "logbook")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int bookId;
    private int readerId;
    private String issueDate;
    private String returnDate;
    private int debt;

    public Log() {}
    public Log(int id, int bookId, int readerId, String issueDate, String returnDate, int debt) {
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
    public String getReturnDate() {
        return returnDate;
    }
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
    public int getDebt() {
        return debt;
    }
    public void setDebt(int debt) {
        this.debt = debt;
    }
}
