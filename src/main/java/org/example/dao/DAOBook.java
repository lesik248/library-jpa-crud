package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.example.model.Book;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOBook extends DAO<Book> {

    private static final Logger logger = Logger.getLogger(DAOBook.class.getName());

    public void create(Book book) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(book);
            tx.commit();
            logger.log(Level.INFO, "Создан Book: {0}", book);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при создании Book: " + book, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public Book read(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Book> query = em.createNamedQuery("Book.findById", Book.class);
            query.setParameter("id", id);
            Book result = query.getSingleResult();
            logger.log(Level.INFO, "Прочитан Book с id={0}: {1}", new Object[]{id, result});
            return result;
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "Book с id={0} не найден.", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при чтении Book с id=" + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Book book) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(book);
            tx.commit();
            logger.log(Level.INFO, "Обновлён Book: {0}", book);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при обновлении Book: " + book, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Book book = em.find(Book.class, id);
            if (book != null) {
                em.remove(book);
                logger.log(Level.INFO, "Удалён Book с id={0}", id);
            } else {
                logger.log(Level.WARNING, "Попытка удалить несуществующий Book с id={0}", id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при удалении Book с id=" + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Book> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Book> query = em.createNamedQuery("Book.findAll", Book.class);
            List<Book> result = query.getResultList();
            logger.log(Level.INFO, "Получено {0} записей Book.", result.size());
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при получении всех записей Book.", e);
            throw e;
        } finally {
            em.close();
        }
    }
}
