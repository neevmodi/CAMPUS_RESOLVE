package com.campusresolve.service;

import com.campusresolve.classifier.*;
import com.campusresolve.dao.*;
import com.campusresolve.exception.DatabaseException;
import com.campusresolve.exception.ComplaintException;
import com.campusresolve.model.*;
import com.campusresolve.util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ComplaintService implements Classifiable {
    private final ComplaintClassifier classifier = new ComplaintClassifier();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final ComplaintHistoryDAO historyDAO = new ComplaintHistoryDAO();

    @Override public ClassificationResult classify(String title, String description) { return classifier.classify(title, description); }
    public long submit(Complaint complaint) {
        validateComplaint(complaint);
        ClassificationResult result = classify(complaint.getTitle(), complaint.getDescription());
        try (Connection connection = DBConnection.open()) {
            connection.setAutoCommit(false);
            try {
                long id = complaintDAO.create(connection, complaint, result);
                historyDAO.add(connection, id, ComplaintStatus.SUBMITTED, complaint.getStudentId(), "Complaint submitted and classified.");
                connection.commit();
                return id;
            } catch (SQLException | RuntimeException exception) { rollback(connection); throw exception; }
        } catch (SQLException exception) { throw new DatabaseException("Unable to submit complaint.", exception); }
    }
    public void updateStatus(long id, ComplaintStatus status, long actorId, String note) {
        ComplaintStatus current = complaintDAO.findStatus(id);
        if (current == null) throw new ComplaintException("Complaint was not found.");
        if (!isValidTransition(current, status)) throw new ComplaintException("That status transition is not allowed.");
        try (Connection connection = DBConnection.open()) {
            connection.setAutoCommit(false);
            try {
                complaintDAO.updateStatus(connection, id, status, note);
                historyDAO.add(connection, id, status, actorId, note);
                connection.commit();
            } catch (SQLException | RuntimeException exception) { rollback(connection); throw exception; }
        } catch (SQLException exception) { throw new DatabaseException("Unable to update complaint.", exception); }
    }
    public void assign(long id, String department, Long staffId, long actorId) {
        ComplaintStatus current = complaintDAO.findStatus(id);
        if (current == null) throw new ComplaintException("Complaint was not found.");
        if (staffId != null && current != ComplaintStatus.ASSIGNED
                && !isValidTransition(current, ComplaintStatus.ASSIGNED)) {
            throw new ComplaintException("A closed or resolved complaint cannot be assigned.");
        }
        if (staffId == null || !new UserDAO().isActiveStaff(staffId)) throw new ComplaintException("Select an active staff member.");
        try (Connection connection = DBConnection.open()) {
            connection.setAutoCommit(false);
            try {
                complaintDAO.assign(connection, id, department, staffId);
                if (current != ComplaintStatus.ASSIGNED) historyDAO.add(connection, id, ComplaintStatus.ASSIGNED, actorId, "Complaint assigned for handling.");
                connection.commit();
            } catch (SQLException | RuntimeException exception) { rollback(connection); throw exception; }
        } catch (SQLException exception) { throw new DatabaseException("Unable to assign complaint.", exception); }
    }
    public static boolean isValidTransition(ComplaintStatus current, ComplaintStatus next) {
        if (current == null || next == null || current == next) return false;
        return switch (current) {
            case SUBMITTED -> next == ComplaintStatus.ANALYZED || next == ComplaintStatus.ASSIGNED;
            case ANALYZED -> next == ComplaintStatus.ASSIGNED || next == ComplaintStatus.IN_PROGRESS;
            case ASSIGNED -> next == ComplaintStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == ComplaintStatus.RESOLVED;
            case RESOLVED -> next == ComplaintStatus.CLOSED;
            case CLOSED -> false;
        };
    }
    public static List<ComplaintStatus> nextStatuses(ComplaintStatus current) {
        return switch (current) {
            case SUBMITTED -> List.of(ComplaintStatus.ANALYZED, ComplaintStatus.ASSIGNED);
            case ANALYZED -> List.of(ComplaintStatus.ASSIGNED, ComplaintStatus.IN_PROGRESS);
            case ASSIGNED -> List.of(ComplaintStatus.IN_PROGRESS);
            case IN_PROGRESS -> List.of(ComplaintStatus.RESOLVED);
            case RESOLVED -> List.of(ComplaintStatus.CLOSED);
            case CLOSED -> List.of();
        };
    }
    public static void validateComplaint(Complaint complaint) {
        if (complaint == null) throw new ComplaintException("Complaint is required.");
        if (complaint.getTitle() == null || complaint.getTitle().isBlank()) throw new ComplaintException("Title is required.");
        if (complaint.getTitle().trim().length() > 180) throw new ComplaintException("Title must be 180 characters or fewer.");
        if (complaint.getDescription() == null || complaint.getDescription().trim().length() < 15) throw new ComplaintException("Description must contain at least 15 characters.");
        if (complaint.getDescription().trim().length() > 10000) throw new ComplaintException("Description is too long.");
        if (complaint.getLocation() == null || complaint.getLocation().isBlank()) throw new ComplaintException("Location is required.");
        if (complaint.getLocation().trim().length() > 180) throw new ComplaintException("Location must be 180 characters or fewer.");
    }
    private static void rollback(Connection connection) {
        try { connection.rollback(); } catch (SQLException ignored) { }
    }
}
