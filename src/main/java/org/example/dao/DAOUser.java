package org.example.dao;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import org.example.model.User;

public class DAOUser {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPADemo");

    public User find(String username) {
        EntityManager em = emf.createEntityManager();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> root = cq.from(User.class);

            cq.select(root)
                    .where(cb.equal(root.get("username"), username));

            return em.createQuery(cq).getResultStream().findFirst().orElse(null);

        } finally {
            em.close();
        }
    }

    public void save(String username, String password) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(new User(username, password));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}