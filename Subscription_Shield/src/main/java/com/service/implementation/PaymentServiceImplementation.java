package com.service.implementation;

import com.entity.Payment;
import com.entity.Subscription;
import com.service.PaymentService;
import com.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;

public class PaymentServiceImplementation implements PaymentService {

    @Override
    public boolean addPayment(Payment payment) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (payment.getSubscription() == null && payment.getSubscriptionId() > 0) {
                Subscription sub = session.get(Subscription.class, payment.getSubscriptionId());
                if (sub == null) {
                    System.err.println("ERROR: Subscription ID " + payment.getSubscriptionId() + " does not exist.");
                    return false;
                }
                payment.setSubscription(sub);
            }

            session.persist(payment);
            tx.commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Subscription ID " + payment.getSubscriptionId() + " does not exist.");
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to add payment - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Payment> getAllPayments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Payment p LEFT JOIN FETCH p.subscription ORDER BY p.paymentId ASC", Payment.class).list();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve payments - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Payment getPaymentById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Payment> list = session.createQuery("FROM Payment p LEFT JOIN FETCH p.subscription WHERE p.paymentId = :id", Payment.class)
                    .setParameter("id", id)
                    .list();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve payment ID " + id + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Payment> getPaymentsBySubscriptionId(int subscriptionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Payment p LEFT JOIN FETCH p.subscription WHERE p.subscription.subscriptionId = :subId ORDER BY p.paymentId ASC", Payment.class)
                    .setParameter("subId", subscriptionId)
                    .list();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve payments for subscription ID " + subscriptionId + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
