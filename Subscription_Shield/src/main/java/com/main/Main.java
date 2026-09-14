package com.main;

import com.entity.Category;
import com.entity.Payment;
import com.entity.Reminder;
import com.entity.Subscription;
import com.service.CategoryService;
import com.service.PaymentService;
import com.service.ReminderService;
import com.service.SubscriptionService;
import com.service.UserService;
import com.service.implementation.CategoryServiceImplementation;
import com.service.implementation.PaymentServiceImplementation;
import com.service.implementation.ReminderServiceImplementation;
import com.service.implementation.SubscriptionServiceImplementation;
import com.service.implementation.UserServiceImplementation;
import com.util.HibernateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final CategoryService categoryService = new CategoryServiceImplementation();
    private static final SubscriptionService subscriptionService = new SubscriptionServiceImplementation();
    private static final PaymentService paymentService = new PaymentServiceImplementation();
    private static final ReminderService reminderService = new ReminderServiceImplementation();
    private static final UserService userService = new UserServiceImplementation();

    public static void main(String[] args) {
        try {
            boolean exit = false;
            while (!exit) {
                printMainMenu();
                int choice = readIntInput("Enter your choice: ");
                System.out.println();
                switch (choice) {
                    case 1:
                        addCategory();
                        break;
                    case 2:
                        viewCategories();
                        break;
                    case 3:
                        addSubscription();
                        break;
                    case 4:
                        viewAllSubscriptions();
                        break;
                    case 5:
                        searchSubscription();
                        break;
                    case 6:
                        updateSubscription();
                        break;
                    case 7:
                        deleteSubscription();
                        break;
                    case 8:
                        deactivateSubscription();
                        break;
                    case 9:
                        addPayment();
                        break;
                    case 10:
                        viewPaymentHistory();
                        break;
                    case 11:
                        addReminder();
                        break;
                    case 12:
                        viewReminders();
                        break;
                    case 13:
                        viewMonthlySpending();
                        break;
                    case 14:
                        viewYearlySpending();
                        break;
                    case 15:
                        viewUpcomingRenewals();
                        break;
                    case 0:
                        exit = true;
                        System.out.println("Thank you for using Subscription Shield! Protecting your finances.");
                        break;
                    default:
                        System.out.println("ERROR: Invalid choice. Please try again.");
                }
                System.out.println();
            }
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void printMainMenu() {
        System.out.println("====================================");
        System.out.println("        SUBSCRIPTION SHIELD        ");
        System.out.println("====================================");
        System.out.println();
        System.out.println("1. Add Category");
        System.out.println("2. View Categories");
        System.out.println();
        System.out.println("3. Add Subscription");
        System.out.println("4. View All Subscriptions");
        System.out.println("5. Search Subscription");
        System.out.println("6. Update Subscription");
        System.out.println("7. Delete Subscription");
        System.out.println("8. Deactivate Subscription");
        System.out.println();
        System.out.println("9. Add Payment");
        System.out.println("10. View Payment History");
        System.out.println();
        System.out.println("11. Add Reminder");
        System.out.println("12. View Reminders");
        System.out.println();
        System.out.println("13. View Monthly Spending");
        System.out.println("14. View Yearly Spending");
        System.out.println("15. View Upcoming Renewals");
        System.out.println();
        System.out.println("0. Exit");
        System.out.println("====================================");
    }

    // --- Category Functionality ---

    private static void addCategory() {
        System.out.println("--- Add Category ---");
        String name = readStringInput("Enter Category Name: ");
        if (name.trim().isEmpty()) {
            System.out.println("ERROR: Category name cannot be empty.");
            return;
        }
        String icon = readStringInput("Enter Icon (optional): ");
        String description = readStringInput("Enter Description (optional): ");

        Category cat = new Category(name, icon, description);
        if (categoryService.addCategory(cat)) {
            System.out.println("SUCCESS: Category added successfully with ID " + cat.getCategoryId());
        }
    }

    private static void viewCategories() {
        System.out.println("--- View Categories ---");
        List<Category> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
        } else {
            System.out.printf("%-5s | %-20s | %-15s | %-30s%n", "ID", "Name", "Icon", "Description");
            System.out.println("-----------------------------------------------------------------------------");
            for (Category c : categories) {
                System.out.printf("%-5d | %-20s | %-15s | %-30s%n",
                        c.getCategoryId(),
                        c.getName(),
                        c.getIcon() != null ? c.getIcon() : "-",
                        c.getDescription() != null ? c.getDescription() : "-");
            }
        }
    }

    // --- Subscription Functionality ---

    private static void addSubscription() {
        System.out.println("--- Add Subscription ---");
        int userId = readIntInput("Enter User ID: ");
        int categoryId = readIntInput("Enter Category ID: ");

        Category cat = categoryService.getCategoryById(categoryId);
        if (cat == null) {
            System.out.println("ERROR: Category does not exist.");
            return;
        }

        String name = readStringInput("Enter Subscription Name: ");
        if (name.trim().isEmpty()) {
            System.out.println("ERROR: Subscription name cannot be empty.");
            return;
        }

        BigDecimal amount = readBigDecimalInput("Enter Amount: ");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("ERROR: Please enter a valid amount.");
            return;
        }

        String cycle = readBillingCycleInput();
        LocalDate startDate = readDateInput("Enter Start Date (yyyy-MM-dd): ");
        if (startDate == null) {
            System.out.println("ERROR: Invalid start date format.");
            return;
        }

        LocalDate nextRenewal = readDateInput("Enter Next Renewal Date (yyyy-MM-dd): ");
        if (nextRenewal == null) {
            System.out.println("ERROR: Invalid next renewal date format.");
            return;
        }

        LocalDate endDate = readOptionalDateInput("Enter End Date (yyyy-MM-dd, optional - press enter to skip): ");
        String notes = readStringInput("Enter Notes (optional): ");

        Subscription sub = new Subscription(userId, categoryId, name, amount, cycle, startDate, nextRenewal, endDate, true, notes);
        if (subscriptionService.addSubscription(sub)) {
            System.out.println("SUCCESS: Subscription added successfully with ID " + sub.getSubscriptionId());
            checkSubscriptionLeaks();
        }
    }

    private static void viewAllSubscriptions() {
        System.out.println("--- View All Subscriptions ---");
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        displaySubscriptionsTable(subscriptions);
    }

    private static void searchSubscription() {
        System.out.println("--- Search Subscription ---");
        int subId = readIntInput("Enter Subscription ID: ");
        Subscription sub = subscriptionService.getSubscriptionById(subId);
        if (sub == null) {
            System.out.println("ERROR: Subscription not found.");
        } else {
            System.out.println("Subscription Details:");
            System.out.println(sub);
        }
    }

    private static void updateSubscription() {
        System.out.println("--- Update Subscription ---");
        int subId = readIntInput("Enter Subscription ID to Update: ");
        Subscription existing = subscriptionService.getSubscriptionById(subId);
        if (existing == null) {
            System.out.println("ERROR: Subscription not found.");
            return;
        }

        System.out.println("Current details: " + existing);
        int userId = readIntInput("Enter User ID [" + existing.getUserId() + "]: ", existing.getUserId());
        int categoryId = readIntInput("Enter Category ID [" + existing.getCategoryId() + "]: ", existing.getCategoryId());

        Category cat = categoryService.getCategoryById(categoryId);
        if (cat == null) {
            System.out.println("ERROR: Category does not exist.");
            return;
        }

        String name = readStringInput("Enter Subscription Name [" + existing.getName() + "]: ");
        if (name.trim().isEmpty()) name = existing.getName();

        BigDecimal amount = readBigDecimalInput("Enter Amount [" + existing.getAmount() + "]: ", existing.getAmount());
        String cycle = readBillingCycleInputWithDefault(existing.getBillingCycle());

        LocalDate startDate = readDateInputWithDefault("Enter Start Date (yyyy-MM-dd) [" + existing.getStartDate() + "]: ", existing.getStartDate());
        LocalDate nextRenewal = readDateInputWithDefault("Enter Next Renewal Date (yyyy-MM-dd) [" + existing.getNextRenewal() + "]: ", existing.getNextRenewal());
        LocalDate endDate = readOptionalDateInput("Enter End Date (yyyy-MM-dd, optional - press enter to keep current): ");
        if (endDate == null) endDate = existing.getEndDate();

        String notes = readStringInput("Enter Notes [" + (existing.getNotes() != null ? existing.getNotes() : "") + "]: ");
        if (notes.trim().isEmpty()) notes = existing.getNotes();

        String activeStr = readStringInput("Is Active? (true/false) [" + existing.isActive() + "]: ");
        boolean isActive = activeStr.trim().isEmpty() ? existing.isActive() : Boolean.parseBoolean(activeStr);

        existing.setUserId(userId);
        existing.setCategoryId(categoryId);
        existing.setName(name);
        existing.setAmount(amount);
        existing.setBillingCycle(cycle);
        existing.setStartDate(startDate);
        existing.setNextRenewal(nextRenewal);
        existing.setEndDate(endDate);
        existing.setActive(isActive);
        existing.setNotes(notes);

        if (subscriptionService.updateSubscription(existing)) {
            System.out.println("SUCCESS: Subscription updated successfully.");
        }
    }

    private static void deleteSubscription() {
        System.out.println("--- Delete Subscription ---");
        int subId = readIntInput("Enter Subscription ID to Delete: ");
        Subscription sub = subscriptionService.getSubscriptionById(subId);
        if (sub == null) {
            System.out.println("ERROR: Subscription not found.");
            return;
        }

        String confirm = readStringInput("Are you sure you want to delete subscription '" + sub.getName() + "'? (yes/no): ");
        if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
            if (subscriptionService.deleteSubscription(subId)) {
                System.out.println("SUCCESS: Subscription deleted successfully.");
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    private static void deactivateSubscription() {
        System.out.println("--- Deactivate Subscription ---");
        int subId = readIntInput("Enter Subscription ID to Deactivate: ");
        Subscription sub = subscriptionService.getSubscriptionById(subId);
        if (sub == null) {
            System.out.println("ERROR: Subscription not found.");
            return;
        }

        if (!sub.isActive()) {
            System.out.println("INFO: Subscription is already inactive.");
            return;
        }

        if (subscriptionService.deactivateSubscription(subId)) {
            System.out.println("SUCCESS: Subscription '" + sub.getName() + "' deactivated successfully.");
        }
    }

    // --- Payment Functionality ---

    private static void addPayment() {
        System.out.println("--- Add Payment ---");
        int subId = readIntInput("Enter Subscription ID: ");
        Subscription sub = subscriptionService.getSubscriptionById(subId);
        if (sub == null) {
            System.out.println("ERROR: Subscription not found.");
            return;
        }

        BigDecimal amount = readBigDecimalInput("Enter Amount: ");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("ERROR: Please enter a valid amount.");
            return;
        }

        LocalDate paymentDate = readDateInput("Enter Payment Date (yyyy-MM-dd): ");
        if (paymentDate == null) {
            System.out.println("ERROR: Invalid payment date.");
            return;
        }

        String method = readStringInput("Enter Payment Method (Credit Card, UPI, Net Banking, etc.): ");
        String txnId = readStringInput("Enter Transaction ID (optional): ");
        String status = readPaymentStatusInput();

        Payment payment = new Payment(subId, amount, paymentDate, method, txnId, status);
        if (paymentService.addPayment(payment)) {
            System.out.println("SUCCESS: Payment recorded successfully with ID " + payment.getPaymentId());
        }
    }

    private static void viewPaymentHistory() {
        System.out.println("--- View Payment History ---");
        List<Payment> payments = paymentService.getAllPayments();
        if (payments.isEmpty()) {
            System.out.println("No payment history records found.");
        } else {
            System.out.printf("%-5s | %-20s | %-12s | %-12s | %-15s | %-20s | %-10s%n",
                    "ID", "Subscription", "Amount", "Date", "Method", "Txn ID", "Status");
            System.out.println("--------------------------------------------------------------------------------------------------");
            for (Payment p : payments) {
                System.out.printf("%-5d | %-20s | ₹%-11.2f | %-12s | %-15s | %-20s | %-10s%n",
                        p.getPaymentId(),
                        p.getSubscriptionName() != null ? p.getSubscriptionName() : String.valueOf(p.getSubscriptionId()),
                        p.getAmount(),
                        p.getPaymentDate(),
                        p.getPaymentMethod() != null ? p.getPaymentMethod() : "-",
                        p.getTransactionId() != null ? p.getTransactionId() : "-",
                        p.getStatus());
            }
        }
    }

    // --- Reminder Functionality ---

    private static void addReminder() {
        System.out.println("--- Add Reminder ---");
        int subId = readIntInput("Enter Subscription ID: ");
        Subscription sub = subscriptionService.getSubscriptionById(subId);
        if (sub == null) {
            System.out.println("ERROR: Subscription not found.");
            return;
        }

        LocalDate reminderDate = readDateInput("Enter Reminder Date (yyyy-MM-dd): ");
        if (reminderDate == null) {
            System.out.println("ERROR: Invalid reminder date.");
            return;
        }

        String channel = readReminderChannelInput();

        Reminder reminder = new Reminder(subId, reminderDate, false, channel);
        if (reminderService.addReminder(reminder)) {
            System.out.println("SUCCESS: Reminder set successfully with ID " + reminder.getReminderId());
        }
    }

    private static void viewReminders() {
        System.out.println("--- View Reminders ---");
        List<Reminder> reminders = reminderService.getAllReminders();
        if (reminders.isEmpty()) {
            System.out.println("No reminders found.");
        } else {
            System.out.printf("%-5s | %-20s | %-12s | %-10s | %-10s%n",
                    "ID", "Subscription", "Date", "Sent Status", "Channel");
            System.out.println("------------------------------------------------------------------");
            for (Reminder r : reminders) {
                System.out.printf("%-5d | %-20s | %-12s | %-11s | %-10s%n",
                        r.getReminderId(),
                        r.getSubscriptionName() != null ? r.getSubscriptionName() : String.valueOf(r.getSubscriptionId()),
                        r.getReminderDate(),
                        r.isSent() ? "Sent" : "Pending",
                        r.getChannel());
            }
        }
    }

    // --- Expense Tracking ---

    private static void viewMonthlySpending() {
        BigDecimal monthly = subscriptionService.getMonthlySpending();
        BigDecimal yearly = subscriptionService.getYearlySpending();
        List<Subscription> allSubs = subscriptionService.getAllSubscriptions();
        long activeCount = allSubs.stream().filter(Subscription::isActive).count();

        System.out.println("====================================");
        System.out.println("       SUBSCRIPTION EXPENSE        ");
        System.out.println("====================================");
        System.out.println();
        System.out.printf("Monthly Spending : ₹%.2f%n", monthly);
        System.out.printf("Yearly Spending  : ₹%.2f%n", yearly);
        System.out.printf("Active Services  : %d%n", activeCount);
        System.out.println();

        checkSubscriptionLeaks();
    }

    private static void viewYearlySpending() {
        viewMonthlySpending();
    }

    // --- Upcoming Renewals & Leak Detection ---

    private static void viewUpcomingRenewals() {
        System.out.println("====================================");
        System.out.println("        UPCOMING RENEWALS          ");
        System.out.println("====================================");
        System.out.println();

        List<Subscription> renewals = subscriptionService.getUpcomingRenewals();
        if (renewals.isEmpty()) {
            System.out.println("No upcoming subscription renewals found.");
        } else {
            for (Subscription sub : renewals) {
                System.out.println(sub.getName());
                System.out.printf("Amount       : ₹%.2f%n", sub.getAmount());
                System.out.printf("Renewal Date : %s%n", sub.getNextRenewal());
                System.out.println();
            }
        }

        checkSubscriptionLeaks();
    }

    /**
     * Rule-based Subscription Leak Detection Logic
     */
    private static void checkSubscriptionLeaks() {
        System.out.println("--- SUBSCRIPTION SHIELD ANALYSIS ---");
        List<Subscription> allSubs = subscriptionService.getAllSubscriptions();

        if (allSubs.isEmpty()) {
            System.out.println("No subscription data available for leak analysis.");
            return;
        }

        boolean leakDetected = false;
        BigDecimal highCostThreshold = new BigDecimal("500.00");
        BigDecimal highTotalMonthlyThreshold = new BigDecimal("2000.00");

        // Rule 1: High Cost Subscription Check
        for (Subscription sub : allSubs) {
            if (sub.isActive() && sub.getAmount() != null) {
                BigDecimal normalizedMonthly = getMonthlyAmountForSub(sub);
                if (normalizedMonthly.compareTo(highCostThreshold) >= 0) {
                    System.out.println("WARNING: Subscription '" + sub.getName() + "' has a high cost (₹" + sub.getAmount() + " / " + sub.getBillingCycle() + ").");
                    leakDetected = true;
                }
            }
        }

        // Rule 2: Multiple Active Subscriptions in Same Category Check
        Map<String, Integer> categoryActiveCounts = new HashMap<>();
        for (Subscription sub : allSubs) {
            if (sub.isActive()) {
                String catName = sub.getCategoryName() != null ? sub.getCategoryName() : "Category #" + sub.getCategoryId();
                categoryActiveCounts.put(catName, categoryActiveCounts.getOrDefault(catName, 0) + 1);
            }
        }
        for (Map.Entry<String, Integer> entry : categoryActiveCounts.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println("WARNING: You have multiple active subscriptions (" + entry.getValue() + ") in the '" + entry.getKey() + "' category.");
                leakDetected = true;
            }
        }

        // Rule 3: Inactive Subscriptions Check
        for (Subscription sub : allSubs) {
            if (!sub.isActive()) {
                System.out.println("INFO: Inactive subscription found: '" + sub.getName() + "'. Consider deleting if no longer needed.");
                leakDetected = true;
            }
        }

        // Rule 4: Approaching Renewal Warning
        LocalDate today = LocalDate.now();
        for (Subscription sub : allSubs) {
            if (sub.isActive() && sub.getNextRenewal() != null) {
                long daysUntilRenewal = java.time.temporal.ChronoUnit.DAYS.between(today, sub.getNextRenewal());
                if (daysUntilRenewal >= 0 && daysUntilRenewal <= 7) {
                    System.out.println("REMINDER: '" + sub.getName() + "' renewal is approaching on " + sub.getNextRenewal() + " (" + daysUntilRenewal + " days remaining).");
                    leakDetected = true;
                }
            }
        }

        // Rule 5: High Recurring Monthly Expense Alert
        BigDecimal totalMonthly = subscriptionService.getMonthlySpending();
        if (totalMonthly.compareTo(highTotalMonthlyThreshold) >= 0) {
            System.out.println("WARNING: Your recurring monthly subscription expense is high (Total: ₹" + String.format("%.2f", totalMonthly) + ").");
            leakDetected = true;
        }

        if (!leakDetected) {
            System.out.println("SUCCESS: No subscription leaks detected. All active services look optimal!");
        }
        System.out.println("------------------------------------");
    }

    private static BigDecimal getMonthlyAmountForSub(Subscription sub) {
        if (sub.getAmount() == null) return BigDecimal.ZERO;
        String cycle = sub.getBillingCycle() != null ? sub.getBillingCycle().toUpperCase() : "MONTHLY";
        switch (cycle) {
            case "WEEKLY":
                return sub.getAmount().multiply(new BigDecimal("52")).divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
            case "YEARLY":
                return sub.getAmount().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
            case "MONTHLY":
            default:
                return sub.getAmount();
        }
    }

    private static void displaySubscriptionsTable(List<Subscription> subscriptions) {
        if (subscriptions.isEmpty()) {
            System.out.println("No subscriptions found.");
        } else {
            System.out.printf("%-5s | %-20s | %-15s | %-12s | %-10s | %-12s | %-12s | %-8s%n",
                    "ID", "Name", "Category", "Amount", "Cycle", "Start Date", "Next Renewal", "Status");
            System.out.println("---------------------------------------------------------------------------------------------------");
            for (Subscription s : subscriptions) {
                System.out.printf("%-5d | %-20s | %-15s | ₹%-11.2f | %-10s | %-12s | %-12s | %-8s%n",
                        s.getSubscriptionId(),
                        s.getName(),
                        s.getCategoryName() != null ? s.getCategoryName() : String.valueOf(s.getCategoryId()),
                        s.getAmount(),
                        s.getBillingCycle(),
                        s.getStartDate(),
                        s.getNextRenewal(),
                        s.isActive() ? "Active" : "Inactive");
            }
        }
    }

    // --- Input Helper Methods ---

    private static String readStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Please enter a valid integer ID/number.");
            }
        }
    }

    private static int readIntInput(String prompt, int defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Keeping current value: " + defaultValue);
            return defaultValue;
        }
    }

    private static BigDecimal readBigDecimalInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return new BigDecimal(input).setScale(2, RoundingMode.HALF_UP);
            } catch (Exception e) {
                System.out.println("ERROR: Please enter a valid monetary amount.");
            }
        }
    }

    private static BigDecimal readBigDecimalInput(String prompt, BigDecimal defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return defaultValue;
        try {
            return new BigDecimal(input).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            System.out.println("Invalid monetary format. Keeping current value: " + defaultValue);
            return defaultValue;
        }
    }

    private static String readBillingCycleInput() {
        while (true) {
            String input = readStringInput("Enter Billing Cycle (WEEKLY / MONTHLY / YEARLY): ").toUpperCase();
            if (input.equals("WEEKLY") || input.equals("MONTHLY") || input.equals("YEARLY")) {
                return input;
            }
            System.out.println("ERROR: Billing cycle must be WEEKLY, MONTHLY, or YEARLY.");
        }
    }

    private static String readBillingCycleInputWithDefault(String defaultValue) {
        String input = readStringInput("Enter Billing Cycle (WEEKLY / MONTHLY / YEARLY) [" + defaultValue + "]: ").toUpperCase();
        if (input.isEmpty()) return defaultValue;
        if (input.equals("WEEKLY") || input.equals("MONTHLY") || input.equals("YEARLY")) {
            return input;
        }
        System.out.println("Invalid cycle choice. Keeping current cycle: " + defaultValue);
        return defaultValue;
    }

    private static String readPaymentStatusInput() {
        while (true) {
            String input = readStringInput("Enter Status (SUCCESS / FAILED / PENDING): ").toUpperCase();
            if (input.equals("SUCCESS") || input.equals("FAILED") || input.equals("PENDING")) {
                return input;
            }
            System.out.println("ERROR: Status must be SUCCESS, FAILED, or PENDING.");
        }
    }

    private static String readReminderChannelInput() {
        while (true) {
            String input = readStringInput("Enter Channel (EMAIL / SMS / APP): ").toUpperCase();
            if (input.equals("EMAIL") || input.equals("SMS") || input.equals("APP")) {
                return input;
            }
            System.out.println("ERROR: Channel must be EMAIL, SMS, or APP.");
        }
    }

    private static LocalDate readDateInput(String prompt) {
        while (true) {
            String input = readStringInput(prompt);
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("ERROR: Please enter a valid date in format yyyy-MM-dd (e.g. 2026-09-01).");
            }
        }
    }

    private static LocalDate readDateInputWithDefault(String prompt, LocalDate defaultValue) {
        String input = readStringInput(prompt);
        if (input.isEmpty()) return defaultValue;
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Keeping current date: " + defaultValue);
            return defaultValue;
        }
    }

    private static LocalDate readOptionalDateInput(String prompt) {
        String input = readStringInput(prompt);
        if (input.isEmpty()) return null;
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Skipping end date.");
            return null;
        }
    }
}
