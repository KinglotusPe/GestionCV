package com.cvmanager.dao.impl;

import com.cvmanager.dao.interfaces.NotificacionDAO;
import com.cvmanager.models.Notificacion;
import com.cvmanager.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionDAOImpl implements NotificacionDAO {

    @Override
    public void create(int userId, String title, String message, String type, String referenceUrl) throws SQLException {
        String sql = "INSERT INTO notificaciones (user_id, title, message, type, reference_url, is_read) VALUES (?, ?, ?, ?, ?, FALSE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.setString(4, type != null ? type : "SISTEMA");
            stmt.setString(5, referenceUrl);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Notificacion> findByUser(int userId, int limit) throws SQLException {
        String sql = "SELECT notification_id, user_id, title, message, type, reference_url, is_read, created_at " +
                     "FROM notificaciones WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        List<Notificacion> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit > 0 ? limit : 50);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notificacion(
                        rs.getInt("notification_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getString("type"),
                        rs.getString("reference_url"),
                        rs.getBoolean("is_read"),
                        rs.getTimestamp("created_at")
                    ));
                }
            }
        }
        return list;
    }

    @Override
    public int countUnread(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notificaciones WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public void markAsRead(int notificationId, int userId) throws SQLException {
        String sql = "UPDATE notificaciones SET is_read = TRUE WHERE notification_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE notificaciones SET is_read = TRUE WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}
