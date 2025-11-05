package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.example.model.Log;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOLog extends DAO<Log> {

    private static final Logger logger = Logger.getLogger(DAOLog.class.getName());

    public void create(Log log) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(log);
            tx.commit();
            logger.log(Level.INFO, "Создан Log: {0}", log);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при создании Log: " + log, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public Log read(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Log> query = em.createNamedQuery("Log.findById", Log.class);
            query.setParameter("id", id);
            Log result = query.getSingleResult();
            logger.log(Level.INFO, "Прочитан Log с id={0}: {1}", new Object[]{id, result});
            return result;
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "Log с id={0} не найден.", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при чтении Log с id=" + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Log log) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(log);
            tx.commit();
            logger.log(Level.INFO, "Обновлён Log: {0}", log);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при обновлении Log: " + log, e);
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
            Log log = em.find(Log.class, id);
            if (log != null) {
                em.remove(log);
                logger.log(Level.INFO, "Удалён Log с id={0}", id);
            } else {
                logger.log(Level.WARNING, "Попытка удалить несуществующий Log с id={0}", id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при удалении Log с id=" + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Log> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Log> query = em.createNamedQuery("Log.findAll", Log.class);
            List<Log> result = query.getResultList();
            logger.log(Level.INFO, "Получено {0} записей Log.", result.size());
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при получении всех записей Log.", e);
            throw e;
        } finally {
            em.close();
        }
    }
}
