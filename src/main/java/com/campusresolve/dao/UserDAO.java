package com.campusresolve.dao;

import com.campusresolve.exception.DatabaseException;
import com.campusresolve.model.*;
import com.campusresolve.util.DBConnection;
import com.campusresolve.util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    public Optional<User> authenticate(String email, String password) {
        String sql = "SELECT id,name,email,role,password_hash FROM users WHERE email=? AND active=TRUE";
        try (Connection connection = DBConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next() || !PasswordUtil.matches(password, result.getString("password_hash"))) {
                    return Optional.empty();
                }
                if (PasswordUtil.needsUpgrade(result.getString("password_hash"))) {
                    upgradePassword(connection, result.getLong("id"), PasswordUtil.hash(password));
                }
                return Optional.of(map(result));
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to authenticate user.", exception);
        }
    }

    public long createStudent(String name, String email, String passwordHash) {
        String sql = "INSERT INTO users(name,email,password_hash,role) VALUES(?,?,?,'STUDENT')";
        try (Connection connection = DBConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            throw new DatabaseException("User was created without an id.", null);
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to create student.", exception);
        }
    }

    public List<User> findStaff() {
        String sql = "SELECT id,name,email,role FROM users WHERE role='STAFF' AND active=TRUE ORDER BY name";
        List<User> result = new ArrayList<>();
        try (Connection connection = DBConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) result.add(map(rows));
            return result;
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to retrieve staff users.", exception);
        }
    }

    public boolean isActiveStaff(long id) {
        String sql = "SELECT 1 FROM users WHERE id=? AND role='STAFF' AND active=TRUE";
        try (Connection connection = DBConnection.open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) { return rows.next(); }
        } catch (SQLException exception) { throw new DatabaseException("Unable to validate staff user.", exception); }
    }

    private void upgradePassword(Connection connection, long id, String passwordHash) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE users SET password_hash=? WHERE id=?")) {
            statement.setString(1, passwordHash);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

    private User map(ResultSet result) throws SQLException {
        long id = result.getLong("id");
        String name = result.getString("name");
        String email = result.getString("email");
        return switch (Role.valueOf(result.getString("role"))) {
            case ADMIN -> new Admin(id, name, email);
            case STAFF -> new Staff(id, name, email);
            case STUDENT -> new Student(id, name, email);
        };
    }
}
