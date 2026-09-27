package com.service.implementation;

import com.entity.Reminder;
import com.entity.Subscription;
import com.service.ReminderService;
import com.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;

public class ReminderServiceImplementation implements ReminderService {

    @Override
    public boolean addReminder(Reminder reminder) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (reminder.getSubscription() == null && reminder.getSubscriptionId() > 0) {
                Subscription sub = session.get(Subscription.class, reminder.getSubscriptionId());
                if (sub == null) {
                    System.err.println("ERROR: Subscription ID " + reminder.getSubscriptionId() + " does not exist.");
                    return false;
                }
                reminder.setSubscription(sub);
            }

            session.persist(reminder);
            tx.commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Subscription ID " + reminder.getSubscriptionId() + " does not exist.");
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to add reminder - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Reminder> getAllReminders() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Reminder r LEFT JOIN FETCH r.subscription ORDER BY r.reminderId ASC", Reminder.class).list();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve reminders - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Reminder> getRemindersBySubscriptionId(int subscriptionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Reminder r LEFT JOIN FETCH r.subscription WHERE r.subscription.subscriptionId = :subId ORDER BY r.reminderId ASC", Reminder.class)
                    .setParameter("subId", subscriptionId)
                    .list();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve reminders for subscription ID " + subscriptionId + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateReminderStatus(int reminderId, boolean isSent) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Reminder reminder = session.get(Reminder.class, reminderId);
            if (reminder != null) {
                reminder.setSent(isSent);
                session.merge(reminder);
                tx.commit();
                return true;
            }
            if (tx != null) tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to update reminder status - " + e.getMessage());
            return false;
        }
    }
}
