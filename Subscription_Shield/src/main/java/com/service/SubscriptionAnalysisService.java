package com.service;

import com.entity.Subscription;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionAnalysisService {

    public List<Subscription> getUnusedSubscriptions(List<Subscription> subscriptions, int unusedDays) {
        List<Subscription> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Subscription sub : subscriptions) {
            if (!sub.isActive() || sub.getLastUsedDate() == null) {
                continue;
            }

            long daysUnused = ChronoUnit.DAYS.between(sub.getLastUsedDate(), today);
            if (daysUnused >= unusedDays) {
                result.add(sub);
            }
        }
        return result;
    }

    public long getUnusedDays(Subscription subscription) {
        if (subscription.getLastUsedDate() == null) {
            return -1;
        }
        return ChronoUnit.DAYS.between(subscription.getLastUsedDate(), LocalDate.now());
    }

    public Map<String, List<Subscription>> findOverlappingSubscriptions(List<Subscription> subscriptions) {
        Map<String, List<Subscription>> groups = new HashMap<>();

        for (Subscription sub : subscriptions) {
            if (!sub.isActive()) {
                continue;
            }

            String group = getServiceGroup(sub);
            if (group != null) {
                groups.computeIfAbsent(group, key -> new ArrayList<>()).add(sub);
            }
        }

        groups.entrySet().removeIf(entry -> entry.getValue().size() < 2);
        return groups;
    }

    private String getServiceGroup(Subscription sub) {
        String name = sub.getName() == null ? "" : sub.getName().toLowerCase();
        String category = sub.getCategoryName() == null ? "" : sub.getCategoryName().toLowerCase();

        if (containsAny(name, "spotify", "youtube music", "apple music", "amazon music", "gaana", "jiosaavn", "wynk")) {
            return "Music Streaming";
        }
        if (containsAny(name, "netflix", "prime video", "amazon prime", "disney", "hotstar", "hulu", "zee5", "sony liv", "jiocinema")) {
            return "Video Streaming";
        }
        if (containsAny(name, "google one", "icloud", "dropbox", "onedrive", "mega")) {
            return "Cloud Storage";
        }
        if (containsAny(name, "xbox", "playstation", "ps plus", "steam", "ea play", "game pass")) {
            return "Gaming";
        }
        if (containsAny(name, "adobe", "microsoft 365", "office 365", "canva", "notion", "grammarly")) {
            return "Productivity / Software";
        }
        if (containsAny(name, "cult", "fitpass", "gym", "fitness")) {
            return "Fitness";
        }

        // Existing category is also treated as an overlap signal.
        if (!category.isEmpty()) {
            return "Category: " + category;
        }

        return null;
    }

    private boolean containsAny(String text, String... values) {
        for (String value : values) {
            if (text.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
