package com.service.implementation;

import com.entity.Category;
import com.entity.Subscription;
import com.entity.User;
import com.service.SubscriptionService;
import com.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionServiceImplementation implements SubscriptionService {

    @Override
    public boolean addSubscription(Subscription subscription) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (subscription.getUser() == null && subscription.getUserId() > 0) {
                User user = session.get(User.class, subscription.getUserId());
                if (user == null) {
                    System.err.println("ERROR: User ID " + subscription.getUserId() + " does not exist.");
                    return false;
                }
                subscription.setUser(user);
            }

            if (subscription.getCategory() == null && subscription.getCategoryId() > 0) {
                Category cat = session.get(Category.class, subscription.getCategoryId());
                if (cat == null) {
                    System.err.println("ERROR: Category ID " + subscription.getCategoryId() + " does not exist.");
                    return false;
                }
                subscription.setCategory(cat);
            }

            session.persist(subscription);
            tx.commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Invalid User ID or Category ID provided.");
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to add subscription - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Subscription s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.user ORDER BY s.subscriptionId ASC", Subscription.class).list();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve subscriptions - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Subscription getSubscriptionById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Subscription> list = session.createQuery("FROM Subscription s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.user WHERE s.subscriptionId = :id", Subscription.class)
                    .setParameter("id", id)
                    .list();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve subscription ID " + id + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Subscription> getSubscriptionsByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Subscription s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.user WHERE s.user.userId = :userId ORDER BY s.subscriptionId ASC", Subscription.class)
                    .setParameter("userId", userId)
                    .list();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve subscriptions for user ID " + userId + " - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateSubscription(Subscription subscription) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (subscription.getUser() == null || subscription.getUser().getUserId() != subscription.getUserId()) {
                User user = session.get(User.class, subscription.getUserId());
                if (user == null) {
                    System.err.println("ERROR: User ID " + subscription.getUserId() + " does not exist.");
                    return false;
                }
                subscription.setUser(user);
            }

            if (subscription.getCategory() == null || subscription.getCategory().getCategoryId() != subscription.getCategoryId()) {
                Category cat = session.get(Category.class, subscription.getCategoryId());
                if (cat == null) {
                    System.err.println("ERROR: Category ID " + subscription.getCategoryId() + " does not exist.");
                    return false;
                }
                subscription.setCategory(cat);
            }

            session.merge(subscription);
            tx.commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Invalid User ID or Category ID provided.");
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to update subscription - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteSubscription(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Subscription sub = session.get(Subscription.class, id);
            if (sub != null) {
                session.remove(sub);
                tx.commit();
                return true;
            }
            if (tx != null) tx.rollback();
            return false;
        } catch (ConstraintViolationException e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Cannot delete subscription ID " + id + " because related payment/reminder records exist. Deactivate it instead.");
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to delete subscription - " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deactivateSubscription(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Subscription sub = session.get(Subscription.class, id);
            if (sub != null) {
                sub.setActive(false);
                session.merge(sub);
                tx.commit();
                return true;
            }
            if (tx != null) tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                tx.rollback();
            }
            System.err.println("ERROR: Failed to deactivate subscription - " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Subscription> getUpcomingRenewals() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Subscription s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.user WHERE s.isActive = true ORDER BY s.nextRenewal ASC", Subscription.class).list();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve upcoming renewals - " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public BigDecimal getMonthlySpending() {
        List<Subscription> activeSubscriptions = getAllSubscriptions();
        BigDecimal totalMonthly = BigDecimal.ZERO;
        for (Subscription sub : activeSubscriptions) {
            if (sub.isActive() && sub.getAmount() != null) {
                String cycle = sub.getBillingCycle() != null ? sub.getBillingCycle().toUpperCase() : "MONTHLY";
                BigDecimal monthlyVal;
                switch (cycle) {
                    case "WEEKLY":
                        monthlyVal = sub.getAmount().multiply(new BigDecimal("52")).divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
                        break;
                    case "YEARLY":
                        monthlyVal = sub.getAmount().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
                        break;
                    case "MONTHLY":
                    default:
                        monthlyVal = sub.getAmount();
                        break;
                }
                totalMonthly = totalMonthly.add(monthlyVal);
            }
        }
        return totalMonthly;
    }

    @Override
    public BigDecimal getYearlySpending() {
        BigDecimal monthly = getMonthlySpending();
        return monthly.multiply(new BigDecimal("12")).setScale(2, RoundingMode.HALF_UP);
    }
}
