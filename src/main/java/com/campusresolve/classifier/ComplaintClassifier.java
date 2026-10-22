package com.campusresolve.classifier;

import com.campusresolve.model.Priority;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Transparent keyword scorer; this is intentionally not presented as machine learning. */
public final class ComplaintClassifier {
    private static final Map<String, String[]> KEYWORDS = new LinkedHashMap<>();
    static {
        KEYWORDS.put("Safety", new String[]{"fire", "smoke", "danger", "emergency", "accident", "security"});
        KEYWORDS.put("Electrical", new String[]{"electric", "electrical", "socket", "wire", "current", "shock", "panel", "short circuit"});
        KEYWORDS.put("IT / Wi-Fi", new String[]{"wifi", "wi-fi", "internet", "network", "computer", "server", "login", "projector"});
        KEYWORDS.put("Plumbing", new String[]{"water", "leak", "pipe", "tap", "washroom", "drain"});
        KEYWORDS.put("Infrastructure", new String[]{"building", "ceiling", "wall", "door", "room", "classroom", "ac", "air conditioner"});
        KEYWORDS.put("Cafeteria", new String[]{"cafeteria", "canteen", "food", "seating", "meal"});
        KEYWORDS.put("Transport", new String[]{"bus", "transport", "shuttle", "route"});
        KEYWORDS.put("Library", new String[]{"library", "book", "reading"});
        KEYWORDS.put("Cleanliness", new String[]{"clean", "garbage", "waste", "dirty", "hygiene"});
        KEYWORDS.put("Academic", new String[]{"course", "exam", "teacher", "class", "grade", "academic"});
    }

    public ClassificationResult classify(String title, String description) {
        String text = ((title == null ? "" : title) + " " + (description == null ? "" : description)).toLowerCase(Locale.ROOT);
        Map<String, Integer> scores = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : KEYWORDS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) if (text.contains(keyword)) score++;
            if (score > 0) scores.put(entry.getKey(), score);
        }
        String category = containsAny(text, "fire", "smoke", "danger", "emergency", "accident")
                ? "Safety"
                : scores.entrySet().stream().max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey).orElse("Other");
        Priority priority = priorityFor(text, category);
        String department = departmentFor(category);
        String explanation = scores.isEmpty() ? "No known keywords matched; routed to Administration."
                : "Matched keywords: " + scores;
        return new ClassificationResult(category, priority, department, explanation);
    }

    private Priority priorityFor(String text, String category) {
        if (containsAny(text, "fire", "smoke", "electric shock", "emergency", "major safety", "danger")) return Priority.CRITICAL;
        if (containsAny(text, "leak", "short circuit", "electrical fault", "broken ac", "air conditioner", "flood")) return Priority.HIGH;
        if (category.equals("IT / Wi-Fi") || category.equals("Infrastructure") || category.equals("Plumbing")) return Priority.MEDIUM;
        if (containsAny(text, "suggestion", "minor", "general request", "improve")) return Priority.LOW;
        return Priority.MEDIUM;
    }

    private String departmentFor(String category) {
        return switch (category) {
            case "IT / Wi-Fi" -> "IT Support";
            case "Safety" -> "Maintenance / Security";
            case "Transport" -> "Transport";
            case "Academic" -> "Academic Affairs";
            case "Library" -> "Library Services";
            case "Cafeteria" -> "Administration";
            default -> "Maintenance";
        };
    }

    private boolean containsAny(String text, String... terms) {
        for (String term : terms) if (text.contains(term)) return true;
        return false;
    }
}
