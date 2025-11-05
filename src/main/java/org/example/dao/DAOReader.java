package org.example.dao;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.example.model.Reader;
import org.example.model.Reader_;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOReader extends DAO<Reader> {

    private static final Logger logger = Logger.getLogger(DAOReader.class.getName());

    public void create(Reader reader) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(reader);
            tx.commit();
            logger.log(Level.INFO, "Создан Reader: {0}", reader);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при создании Reader: " + reader, e);
            throw new PersistenceException("Не удалось создать Reader: " + reader, e);
        } finally {
            em.close();
        }
    }

    public Reader read(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Reader> cq = cb.createQuery(Reader.class);
            Root<Reader> root = cq.from(Reader.class);
            cq.select(root).where(cb.equal(root.get(Reader_.id), id));

            TypedQuery<Reader> query = em.createQuery(cq);
            Reader result = query.getSingleResult();

            logger.log(Level.INFO, "Прочитан Reader с id={0}: {1}", new Object[]{id, result});
            return result;
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "Reader с id={0} не найден.", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при чтении Reader с id=" + id, e);
            throw new PersistenceException("Не удалось прочитать Reader с id=" + id, e);
        } finally {
            em.close();
        }
    }

    public void update(Reader reader) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(reader);
            tx.commit();
            logger.log(Level.INFO, "Обновлён Reader: {0}", reader);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при обновлении Reader: " + reader, e);
            throw new PersistenceException("Не удалось обновить Reader: " + reader, e);
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<Reader> delete = cb.createCriteriaDelete(Reader.class);
            Root<Reader> root = delete.from(Reader.class);
            delete.where(cb.equal(root.get(Reader_.id), id));

            int deleted = em.createQuery(delete).executeUpdate();

            if (deleted > 0) {
                logger.log(Level.INFO, "Удалён Reader с id={0}", id);
            } else {
                logger.log(Level.WARNING, "Попытка удалить несуществующий Reader с id={0}", id);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при удалении Reader с id=" + id, e);
            throw new PersistenceException("Не удалось удалить Reader с id=" + id, e);
        } finally {
            em.close();
        }
    }

    public List<Reader> getAll() {
        EntityManager em = emf.createEntityManager();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Reader> cq = cb.createQuery(Reader.class);
            Root<Reader> root = cq.from(Reader.class);
            cq.select(root);

            TypedQuery<Reader> query = em.createQuery(cq);
            List<Reader> result = query.getResultList();

            logger.log(Level.INFO, "Получено {0} записей Reader.", result.size());
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при получении всех записей Reader.", e);
            throw new PersistenceException("Не удалось получить список Reader.", e);
        } finally {
            em.close();
        }
    }
}
