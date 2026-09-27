package com.service;

import com.entity.Reminder;

import java.util.List;

public interface ReminderService {

    boolean addReminder(Reminder reminder);

    List<Reminder> getAllReminders();

    List<Reminder> getRemindersBySubscriptionId(int subscriptionId);

    boolean updateReminderStatus(int reminderId, boolean isSent);
}
