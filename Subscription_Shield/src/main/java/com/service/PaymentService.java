package com.service;

import com.entity.Payment;

import java.util.List;

public interface PaymentService {

    boolean addPayment(Payment payment);

    List<Payment> getAllPayments();

    Payment getPaymentById(int id);

    List<Payment> getPaymentsBySubscriptionId(int subscriptionId);
}
