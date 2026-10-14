package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.models.User;

/**
 * Implementación JDBC de la interfaz UsuarioDAO.
 * Administra el acceso a datos para la tabla 'usuarios'.
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    /**
     * Mapea una fila del ResultSet a un objeto POJO User.
     */
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(resultSet.getString("role"));
        user.setStatus(resultSet.getString("status"));
        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        user.setLastLogin(resultSet.getTimestamp("last_login"));
        return user;
    }

    @Override
    public User findByEmail(String email) throws Exception {
        String sql = "SELECT user_id, email, password_hash, role, status, created_at, last_login " +
                     "FROM usuarios WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public User findById(int userId) throws Exception {
        String sql = "SELECT user_id, email, password_hash, role, status, created_at, last_login " +
                     "FROM usuarios WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public int create(Connection connection, User user) throws Exception {
        String sql = "INSERT INTO usuarios(email, password_hash, role, status) VALUES(?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getRole());
            statement.setString(4, user.getStatus());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo generar el identificador (user_id) para el usuario.");
    }

    @Override
    public List<User> findAll(String role, String status) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT user_id, email, password_hash, role, status, created_at, last_login FROM usuarios WHERE 1=1");
        List<String> parameters = new ArrayList<>();

        if (role != null && !role.isBlank()) {
            sql.append(" AND role = ?");
            parameters.add(role);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ?");
            parameters.add(status);
        }
        sql.append(" ORDER BY created_at DESC");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setString(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                List<User> userList = new ArrayList<>();
                while (resultSet.next()) {
                    userList.add(mapResultSetToUser(resultSet));
                }
                return userList;
            }
        }
    }

    @Override
    public void updateStatus(int userId, String status) throws Exception {
        String sql = "UPDATE usuarios SET status = ? WHERE user_id = ? AND role <> 'admin'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    @Override
    public void touchLogin(int userId) throws Exception {
        String sql = "UPDATE usuarios SET last_login = NOW() WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    @Override
    public boolean hasAdmin() throws Exception {
        String sql = "SELECT 1 FROM usuarios WHERE role = 'admin' LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            return resultSet.next();
        }
    }
}
