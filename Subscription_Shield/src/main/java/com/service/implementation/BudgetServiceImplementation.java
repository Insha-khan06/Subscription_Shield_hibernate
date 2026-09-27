package com.service.implementation;

import com.entity.Budget;
import com.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class BudgetServiceImplementation implements com.service.BudgetService {

    public boolean setMonthlyBudget(int userId, BigDecimal monthlyBudget) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<Budget> budgets = session.createQuery(
                    "FROM Budget b WHERE b.userId = :userId ORDER BY b.budgetId ASC",
                    Budget.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .list();

            Budget budget;
            if (budgets.isEmpty()) {
                budget = new Budget(userId, monthlyBudget);
                session.persist(budget);
            } else {
                budget = budgets.get(0);
                budget.setMonthlyBudget(monthlyBudget);
                session.merge(budget);
            }

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to save monthly budget - " + e.getMessage());
            return false;
        }
    }

    public Budget getBudgetByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Budget> budgets = session.createQuery(
                    "FROM Budget b WHERE b.userId = :userId ORDER BY b.budgetId ASC",
                    Budget.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .list();
            return budgets.isEmpty() ? null : budgets.get(0);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve budget - " + e.getMessage());
            return null;
        }
    }
}
