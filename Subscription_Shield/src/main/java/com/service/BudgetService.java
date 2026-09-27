package com.service;

import com.entity.Budget;

import java.math.BigDecimal;

public interface BudgetService {

    boolean setMonthlyBudget(int userId, BigDecimal monthlyBudget);

    Budget getBudgetByUserId(int userId);
}
