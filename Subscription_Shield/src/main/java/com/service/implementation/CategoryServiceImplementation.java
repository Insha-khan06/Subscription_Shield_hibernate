package com.service.implementation;

import com.entity.Category;
import com.service.CategoryService;
import com.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;

public class CategoryServiceImplementation implements CategoryService {

    @Override
    public boolean addCategory(Category category) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(category);
            tx.commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: A category with the name '" + category.getName() + "' already exists.");
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to add category - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Category> getAllCategories() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Category ORDER BY categoryId ASC", Category.class).list();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve categories - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Category getCategoryById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Category.class, id);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve category ID " + id + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean updateCategory(Category category) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(category);
            tx.commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Category name '" + category.getName() + "' already exists.");
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to update category - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCategory(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Category category = session.get(Category.class, id);
            if (category != null) {
                session.remove(category);
                tx.commit();
                return true;
            }
            if (tx != null) tx.rollback();
            return false;
        } catch (ConstraintViolationException e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Cannot delete category ID " + id + " because subscriptions are linked to it.");
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to delete category - " + e.getMessage());
            return false;
        }
    }
}
