package com.service.implementation;

import com.entity.User;
import com.service.UserService;
import com.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImplementation implements UserService {

    @Override
    public boolean addUser(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to add user - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User ORDER BY userId ASC", User.class).list();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve users - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public User getUserById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve user ID " + id + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean updateUser(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to update user - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteUser(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                tx.commit();
                return true;
            }
            if (tx != null) tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to delete user - " + e.getMessage());
            return false;
        }
    }
}
