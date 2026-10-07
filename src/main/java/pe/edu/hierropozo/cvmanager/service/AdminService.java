package pe.edu.hierropozo.cvmanager.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.hierropozo.cvmanager.config.DBConnection;

/**
 * Servicio de métricas, auditoría y consultas analíticas para el panel del administrador.
 */
public class AdminService {

    /**
     * Obtiene los indicadores estadísticos principales del sistema.
     */
    public Map<String, Integer> stats() throws Exception {
        Map<String, Integer> statistics = new LinkedHashMap<>();

        String[][] queries = new String[][]{
            {"graduates", "SELECT COUNT(*) FROM egresados"},
            {"companies", "SELECT COUNT(*) FROM empresas"},
            {"published", "SELECT COUNT(*) FROM cv WHERE is_published = 1"},
            {"pending", "SELECT COUNT(*) FROM usuarios WHERE status = 'pending'"}
        };

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {

            for (String[] entry : queries) {
                String metricKey = entry[0];
                String querySql = entry[1];

                try (ResultSet resultSet = statement.executeQuery(querySql)) {
                    if (resultSet.next()) {
                        statistics.put(metricKey, resultSet.getInt(1));
                    }
                }
            }
        }
        return statistics;
    }

    /**
     * Obtiene la distribución de egresados agrupada por programa de estudios (carrera).
     */
    public List<Map<String, Object>> byCareer() throws Exception {
        String sql = "SELECT c.name, COUNT(g.graduate_id) AS total " +
                     "FROM carreras c " +
                     "LEFT JOIN egresados g ON g.career_id = c.career_id " +
                     "GROUP BY c.career_id, c.name " +
                     "ORDER BY total DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            List<Map<String, Object>> list = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("name", resultSet.getString("name"));
                row.put("total", resultSet.getInt("total"));
                list.add(row);
            }
            return list;
        }
    }

    /**
     * Obtiene el historial de registros de auditoría del sistema con datos del usuario.
     */
    public List<Map<String, Object>> getAuditLogs(int limit) throws Exception {
        String sql = "SELECT l.log_id, l.user_id, l.action, l.entity_type, l.entity_id, " +
                     "l.details, l.ip_address, l.created_at, u.email, u.role " +
                     "FROM logs_auditoria l " +
                     "LEFT JOIN usuarios u ON u.user_id = l.user_id " +
                     "ORDER BY l.created_at DESC LIMIT ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }
                    list.add(row);
                }
                return list;
            }
        }
    }
}
