package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reader")
@NamedQueries({
        @NamedQuery(
                name = "Reader.findById",
                query = "SELECT r FROM Reader r WHERE r.id = :id"
        ),
        @NamedQuery(
                name = "Reader.findAll",
                query = "SELECT r FROM Reader r"
        )
})
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    public Reader() {}

    public Reader(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}