package com.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private int subscriptionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "billing_cycle", nullable = false)
    private String billingCycle; // WEEKLY, MONTHLY, YEARLY

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "next_renewal", nullable = false)
    private LocalDate nextRenewal;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "notes", length = 500)
    private String notes;

    @Transient
    private Integer transientUserId;

    @Transient
    private Integer transientCategoryId;

    public Subscription() {
    }

    public Subscription(int subscriptionId, User user, Category category, String name, BigDecimal amount,
                        String billingCycle, LocalDate startDate, LocalDate nextRenewal, LocalDate endDate,
                        boolean isActive, String notes) {
        this.subscriptionId = subscriptionId;
        this.user = user;
        this.category = category;
        this.name = name;
        this.amount = amount;
        this.billingCycle = billingCycle;
        this.startDate = startDate;
        this.nextRenewal = nextRenewal;
        this.endDate = endDate;
        this.isActive = isActive;
        this.notes = notes;
    }

    public Subscription(int userId, int categoryId, String name, BigDecimal amount,
                        String billingCycle, LocalDate startDate, LocalDate nextRenewal, LocalDate endDate,
                        boolean isActive, String notes) {
        this.transientUserId = userId;
        this.transientCategoryId = categoryId;
        this.name = name;
        this.amount = amount;
        this.billingCycle = billingCycle;
        this.startDate = startDate;
        this.nextRenewal = nextRenewal;
        this.endDate = endDate;
        this.isActive = isActive;
        this.notes = notes;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getUserId() {
        if (user != null) {
            return user.getUserId();
        }
        return transientUserId != null ? transientUserId : 0;
    }

    public void setUserId(int userId) {
        this.transientUserId = userId;
        if (user != null) {
            user.setUserId(userId);
        }
    }

    public int getCategoryId() {
        if (category != null) {
            return category.getCategoryId();
        }
        return transientCategoryId != null ? transientCategoryId : 0;
    }

    public void setCategoryId(int categoryId) {
        this.transientCategoryId = categoryId;
        if (category != null) {
            category.setCategoryId(categoryId);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getNextRenewal() {
        return nextRenewal;
    }

    public void setNextRenewal(LocalDate nextRenewal) {
        this.nextRenewal = nextRenewal;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }

    public String getUserName() {
        return user != null ? user.getName() : null;
    }

    @Override
    public String toString() {
        String catDisplay = (getCategoryName() != null && !getCategoryName().trim().isEmpty()) ? getCategoryName() : String.valueOf(getCategoryId());
        return String.format("Subscription [ID: %d | Name: %s | Category: %s | Amount: ₹%.2f | Cycle: %s | Start: %s | Next Renewal: %s | End: %s | Active: %s | Notes: %s]",
                subscriptionId, name, catDisplay, amount, billingCycle, startDate, nextRenewal,
                endDate != null ? endDate.toString() : "None", isActive ? "Yes" : "No", notes != null ? notes : "N/A");
    }
}
