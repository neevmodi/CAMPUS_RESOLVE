package com.campusresolve.classifier;

import com.campusresolve.model.Priority;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ComplaintClassifierTest {
    private final ComplaintClassifier classifier = new ComplaintClassifier();
    @Test void identifiesWifiIssue() {
        ClassificationResult result = classifier.classify("Wi-Fi outage", "The Wi-Fi is not working in Block B.");
        assertEquals("IT / Wi-Fi", result.category());
        assertEquals(Priority.MEDIUM, result.priority());
        assertEquals("IT Support", result.department());
    }
    @Test void identifiesCriticalElectricalSafetyIssue() {
        ClassificationResult result = classifier.classify("Smoke in lab", "Smoke is coming from the electrical panel in Lab 301.");
        assertEquals(Priority.CRITICAL, result.priority());
        assertEquals("Safety", result.category());
        assertEquals("Maintenance / Security", result.department());
    }
    @Test void identifiesHighPlumbingIssue() {
        ClassificationResult result = classifier.classify("Ceiling leak", "Water is leaking from the ceiling of Room 204.");
        assertEquals("Plumbing", result.category());
        assertEquals(Priority.HIGH, result.priority());
        assertEquals("Maintenance", result.department());
    }
    @Test void explainsUnmatchedRequest() {
        ClassificationResult result = classifier.classify("Suggestion", "A general suggestion to improve campus life.");
        assertEquals(Priority.LOW, result.priority());
        assertTrue(result.explanation().contains("No known keywords"));
    }
}
