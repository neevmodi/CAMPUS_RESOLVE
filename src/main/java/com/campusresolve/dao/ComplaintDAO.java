package com.campusresolve.dao;

import com.campusresolve.classifier.ClassificationResult;
import com.campusresolve.exception.DatabaseException;
import com.campusresolve.model.*;
import com.campusresolve.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComplaintDAO {
    public long create(Complaint complaint, ClassificationResult classification) {
        String sql = "INSERT INTO complaints(student_id,title,description,location,category,priority,department,status,classification_explanation) VALUES(?,?,?,?,?,?,?,'SUBMITTED',?)";
        try (Connection connection = DBConnection.open()) {
            return create(connection, complaint, classification);
        } catch (SQLException exception) { throw new DatabaseException("Unable to save complaint.", exception); }
    }
    public long create(Connection connection, Complaint complaint, ClassificationResult classification) throws SQLException {
        String sql = "INSERT INTO complaints(student_id,title,description,location,category,priority,department,status,classification_explanation) VALUES(?,?,?,?,?,?,?,'SUBMITTED',?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, complaint.getStudentId()); statement.setString(2, complaint.getTitle());
            statement.setString(3, complaint.getDescription()); statement.setString(4, complaint.getLocation());
            statement.setString(5, classification.category()); statement.setString(6, classification.priority().name());
            statement.setString(7, classification.department()); statement.setString(8, classification.explanation());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { if (keys.next()) return keys.getLong(1); }
            throw new DatabaseException("Complaint was created without an id.", null);
        }
    }
    public List<Complaint> findByStudent(long studentId) { return find("WHERE student_id=? ORDER BY created_at DESC", studentId); }
    public List<Complaint> findAll() { return find("ORDER BY created_at DESC"); }
    public List<Complaint> search(String query, String status, String priority) {
        return searchInternal(null, query, status, priority);
    }
    public List<Complaint> searchAssignedTo(long staffId, String query, String status, String priority) {
        return searchInternal(staffId, query, status, priority);
    }
    private List<Complaint> searchInternal(Long staffId, String query, String status, String priority) {
        StringBuilder suffix = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (staffId != null) { suffix.append(" AND c.assigned_staff_id=?"); params.add(staffId); }
        if (query != null && !query.isBlank()) {
            suffix.append(" AND (LOWER(c.title) LIKE ? OR LOWER(c.description) LIKE ? OR LOWER(c.location) LIKE ?)");
            String pattern = "%" + query.trim().toLowerCase() + "%";
            params.add(pattern); params.add(pattern); params.add(pattern);
        }
        if (status != null && !status.isBlank()) { suffix.append(" AND c.status=?"); params.add(status); }
        if (priority != null && !priority.isBlank()) { suffix.append(" AND c.priority=?"); params.add(priority); }
        suffix.append(" ORDER BY c.created_at DESC");
        return find(suffix.toString(), params.toArray());
    }
    public boolean isAssignedTo(long complaintId, long staffId) {
        String sql = "SELECT 1 FROM complaints WHERE id=? AND assigned_staff_id=?";
        try (Connection connection = DBConnection.open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, complaintId); statement.setLong(2, staffId);
            try (ResultSet result = statement.executeQuery()) { return result.next(); }
        } catch (SQLException exception) { throw new DatabaseException("Unable to validate complaint assignment.", exception); }
    }
    public Optional<Complaint> findById(long id) {
        List<Complaint> complaints = find("WHERE c.id=?", id);
        return complaints.stream().findFirst();
    }
    public void updateStatus(long id, ComplaintStatus status, String notes) {
        try (Connection connection = DBConnection.open()) { updateStatus(connection, id, status, notes); }
        catch (SQLException exception) { throw new DatabaseException("Unable to update complaint status.", exception); }
    }
    public void updateStatus(Connection connection, long id, ComplaintStatus status, String notes) throws SQLException {
        String sql = "UPDATE complaints SET status=?,resolution_notes=?,updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name()); statement.setString(2, notes); statement.setLong(3, id); statement.executeUpdate();
        }
    }
    public void assign(long id, String department, Long staffId) {
        try (Connection connection = DBConnection.open()) { assign(connection, id, department, staffId); }
        catch (SQLException exception) { throw new DatabaseException("Unable to assign complaint.", exception); }
    }
    public void assign(Connection connection, long id, String department, Long staffId) throws SQLException {
        String sql = "UPDATE complaints SET department=?,assigned_staff_id=?,status='ASSIGNED',updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, department); if (staffId == null) statement.setNull(2, Types.BIGINT); else statement.setLong(2, staffId);
            statement.setLong(3, id); statement.executeUpdate();
        }
    }
    public ComplaintStatus findStatus(long id) {
        String sql = "SELECT status FROM complaints WHERE id=?";
        try (Connection connection = DBConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return null;
                return ComplaintStatus.valueOf(result.getString("status"));
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to retrieve complaint status.", exception);
        }
    }
    private List<Complaint> find(String suffix, Object... params) {
        String sql = "SELECT c.* FROM complaints c " + suffix;
        List<Complaint> results = new ArrayList<>();
        try (Connection connection = DBConnection.open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) statement.setObject(i + 1, params[i]);
            try (ResultSet result = statement.executeQuery()) { while (result.next()) results.add(map(result)); }
            return results;
        } catch (SQLException exception) { throw new DatabaseException("Unable to retrieve complaints.", exception); }
    }
    private Complaint map(ResultSet result) throws SQLException {
        Complaint c = new Complaint(result.getLong("student_id"), result.getString("title"), result.getString("description"), result.getString("location"));
        c.setId(result.getLong("id")); c.setCategory(result.getString("category")); c.setPriority(Priority.valueOf(result.getString("priority")));
        c.setDepartment(result.getString("department")); c.setStatus(ComplaintStatus.valueOf(result.getString("status"))); c.setResolutionNotes(result.getString("resolution_notes"));
        long staff = result.getLong("assigned_staff_id"); if (!result.wasNull()) c.setAssignedStaffId(staff);
        Timestamp created = result.getTimestamp("created_at"); if (created != null) c.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = result.getTimestamp("updated_at"); if (updated != null) c.setUpdatedAt(updated.toLocalDateTime());
        return c;
    }
}
