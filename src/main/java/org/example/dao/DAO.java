package org.example.dao;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.SQLException;
import java.util.List;

public abstract class DAO<T> {
    protected EntityManagerFactory emf;

    public DAO() {
        emf = Persistence.createEntityManagerFactory("JPADemo");
    }
    public abstract void create(T item) throws SQLException;
    public abstract T read(int id) throws SQLException;
    public abstract void update(T entity) throws SQLException;
    public abstract void delete(int id) throws SQLException;
    public abstract List<T> getAll() throws SQLException;

}
