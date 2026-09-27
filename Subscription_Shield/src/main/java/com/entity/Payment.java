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
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "transaction_id", length = 150)
    private String transactionId;

    @Column(name = "status")
    private String status; // SUCCESS, FAILED, PENDING

    @Transient
    private Integer transientSubscriptionId;

    public Payment() {
    }

    public Payment(int paymentId, Subscription subscription, BigDecimal amount, LocalDate paymentDate,
                   String paymentMethod, String transactionId, String status) {
        this.paymentId = paymentId;
        this.subscription = subscription;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
    }

    public Payment(int subscriptionId, BigDecimal amount, LocalDate paymentDate,
                   String paymentMethod, String transactionId, String status) {
        this.transientSubscriptionId = subscriptionId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubscriptionName() {
        return subscription != null ? subscription.getName() : null;
    }

    @Override
    public String toString() {
        String subDisplay = (getSubscriptionName() != null && !getSubscriptionName().trim().isEmpty()) ? getSubscriptionName() : String.valueOf(getSubscriptionId());
        return String.format("Payment [ID: %d | Subscription: %s | Amount: ₹%.2f | Date: %s | Method: %s | TxnID: %s | Status: %s]",
                paymentId, subDisplay, amount, paymentDate,
                paymentMethod != null ? paymentMethod : "N/A",
                transactionId != null ? transactionId : "N/A", status);
    }
}
