package pe.edu.hierropozo.cvmanager.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import pe.edu.hierropozo.cvmanager.config.DBConnection;

/**
 * Utilidad para el registro de eventos y auditoría en la tabla 'logs_auditoria'.
 */
public final class AuditUtil {

    private AuditUtil() {
        // Constructor privado
    }

    /**
     * Registra una acción de auditoría en la base de datos de manera resiliente.
     * @param userId ID del usuario que ejecuta la acción (o null si es anónimo)
     * @param action Código de acción (LOGIN, LOGOUT, UPDATE_CV, etc.)
     * @param entityType Tipo de entidad afectada ('usuario', 'cv', 'carrera', etc.)
     * @param entityId ID de la entidad afectada
     * @param details Descripción adicional
     * @param ipAddress Dirección IP de origen
     */
    public static void log(Integer userId, String action, String entityType, Integer entityId, String details, String ipAddress) {
        String sql = "INSERT INTO logs_auditoria(user_id, action, entity_type, entity_id, details, ip_address) " +
                     "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userId == null) {
                statement.setNull(1, Types.INTEGER);
            } else {
                statement.setInt(1, userId);
            }

            statement.setString(2, action);
            statement.setString(3, entityType);

            if (entityId == null) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, entityId);
            }

            statement.setString(5, details);
            statement.setString(6, ipAddress);

            statement.executeUpdate();
        } catch (SQLException ignored) {
            // El fallo de auditoría no debe interrumpir el flujo principal de la aplicación
        }
    }
}
