package com.cvmanager.dao.interfaces;

import com.cvmanager.models.Notificacion;
import java.sql.SQLException;
import java.util.List;

public interface NotificacionDAO {
    void create(int userId, String title, String message, String type, String referenceUrl) throws SQLException;
    List<Notificacion> findByUser(int userId, int limit) throws SQLException;
    int countUnread(int userId) throws SQLException;
    void markAsRead(int notificationId, int userId) throws SQLException;
    void markAllAsRead(int userId) throws SQLException;
}
