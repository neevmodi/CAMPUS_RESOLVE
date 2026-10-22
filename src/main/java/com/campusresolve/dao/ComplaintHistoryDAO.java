package com.campusresolve.dao;

import com.campusresolve.exception.DatabaseException;
import com.campusresolve.model.ComplaintHistoryEntry;
import com.campusresolve.model.ComplaintStatus;
import com.campusresolve.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintHistoryDAO {
    public void add(long complaintId, ComplaintStatus status, long changedBy, String note) {
        try (Connection connection = DBConnection.open()) {
            add(connection, complaintId, status, changedBy, note);
        } catch (SQLException exception) { throw new DatabaseException("Unable to record complaint history.", exception); }
    }
    public void add(Connection connection, long complaintId, ComplaintStatus status, long changedBy, String note) throws SQLException {
        String sql = "INSERT INTO complaint_history(complaint_id,status,changed_by,note) VALUES(?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, complaintId); statement.setString(2, status.name()); statement.setLong(3, changedBy); statement.setString(4, note);
            statement.executeUpdate();
        }
    }

    public List<ComplaintHistoryEntry> findByComplaint(long complaintId) {
        String sql = "SELECT status,changed_by,note,changed_at FROM complaint_history "
                + "WHERE complaint_id=? ORDER BY changed_at ASC, id ASC";
        List<ComplaintHistoryEntry> entries = new ArrayList<>();
        try (Connection connection = DBConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, complaintId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    entries.add(new ComplaintHistoryEntry(
                            ComplaintStatus.valueOf(result.getString("status")),
                            result.getLong("changed_by"),
                            result.getString("note"),
                            result.getTimestamp("changed_at").toLocalDateTime()));
                }
            }
            return entries;
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to retrieve complaint history.", exception);
        }
    }
}
