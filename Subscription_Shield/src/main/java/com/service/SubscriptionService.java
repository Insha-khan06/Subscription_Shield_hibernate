package com.service;

import com.entity.Subscription;

import java.math.BigDecimal;
import java.util.List;

public interface SubscriptionService {

    boolean addSubscription(Subscription subscription);

    List<Subscription> getAllSubscriptions();

    Subscription getSubscriptionById(int id);

    List<Subscription> getSubscriptionsByUserId(int userId);

    boolean updateSubscription(Subscription subscription);

    boolean deleteSubscription(int id);

    boolean deactivateSubscription(int id);

    List<Subscription> getUpcomingRenewals();

    BigDecimal getMonthlySpending();

    BigDecimal getYearlySpending();
}
