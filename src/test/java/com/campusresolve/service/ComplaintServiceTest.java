package com.campusresolve.service;

import com.campusresolve.exception.ComplaintException;
import com.campusresolve.model.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ComplaintServiceTest {
    @Test void permitsForwardComplaintLifecycleTransitions() {
        assertTrue(ComplaintService.isValidTransition(ComplaintStatus.SUBMITTED, ComplaintStatus.ANALYZED));
        assertTrue(ComplaintService.isValidTransition(ComplaintStatus.IN_PROGRESS, ComplaintStatus.RESOLVED));
        assertTrue(ComplaintService.isValidTransition(ComplaintStatus.RESOLVED, ComplaintStatus.CLOSED));
        assertFalse(ComplaintService.isValidTransition(ComplaintStatus.CLOSED, ComplaintStatus.IN_PROGRESS));
        assertFalse(ComplaintService.isValidTransition(ComplaintStatus.SUBMITTED, ComplaintStatus.CLOSED));
    }

    @Test void validatesRequiredComplaintFields() {
        Complaint complaint = new Complaint(1, "", "Too short", "");
        ComplaintException error = assertThrows(ComplaintException.class,
                () -> ComplaintService.validateComplaint(complaint));
        assertEquals("Title is required.", error.getMessage());
        assertThrows(ComplaintException.class,
                () -> ComplaintService.validateComplaint(new Complaint(1, "Wi-Fi", "Short", "Block B")));
    }
}
