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

import java.time.LocalDate;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_id")
    private int reminderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(name = "reminder_date", nullable = false)
    private LocalDate reminderDate;

    @Column(name = "is_sent")
    private boolean isSent = false;

    @Column(name = "channel")
    private String channel; // EMAIL, SMS, APP

    @Transient
    private Integer transientSubscriptionId;

    public Reminder() {
    }

    public Reminder(int reminderId, Subscription subscription, LocalDate reminderDate, boolean isSent, String channel) {
        this.reminderId = reminderId;
        this.subscription = subscription;
        this.reminderDate = reminderDate;
        this.isSent = isSent;
        this.channel = channel;
    }

    public Reminder(int subscriptionId, LocalDate reminderDate, boolean isSent, String channel) {
        this.transientSubscriptionId = subscriptionId;
        this.reminderDate = reminderDate;
        this.isSent = isSent;
        this.channel = channel;
    }

    public int getReminderId() {
        return reminderId;
    }

    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public int getSubscriptionId() {
        if (subscription != null) {
            return subscription.getSubscriptionId();
        }
        return transientSubscriptionId != null ? transientSubscriptionId : 0;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.transientSubscriptionId = subscriptionId;
        if (subscription != null) {
            subscription.setSubscriptionId(subscriptionId);
        }
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSubscriptionName() {
        return subscription != null ? subscription.getName() : null;
    }

    @Override
    public String toString() {
        String subDisplay = (getSubscriptionName() != null && !getSubscriptionName().trim().isEmpty()) ? getSubscriptionName() : String.valueOf(getSubscriptionId());
        return String.format("Reminder [ID: %d | Subscription: %s | Date: %s | Sent: %s | Channel: %s]",
                reminderId, subDisplay, reminderDate, isSent ? "Yes" : "No", channel);
    }
}
