package pe.edu.hierropozo.cvmanager.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.hierropozo.cvmanager.config.DBConnection;

/**
 * Servicio de búsqueda avanzada y acciones de empleabilidad para empresas
 * (filtrado en SQL, favoritos, solicitudes de contacto y registro de visualizaciones).
 */
public class SearchService {

    /**
     * Realiza búsquedas parametrizadas directamente en MySQL con paginación.
     */
    public List<Map<String, Object>> search(String query, String careerId, String skill, String city, int page, int pageSize) throws Exception {
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT cv.cv_id, g.graduate_id, g.first_name, g.last_name, g.city, " +
            "c.name AS career, cv.title, cv.professional_summary, cv.views_count " +
            "FROM cv " +
            "JOIN egresados g ON g.graduate_id = cv.graduate_id " +
            "LEFT JOIN carreras c ON c.career_id = g.career_id " +
            "LEFT JOIN habilidades h ON h.cv_id = cv.cv_id " +
            "WHERE cv.is_published = 1 AND g.is_public = 1"
        );

        List<Object> parameters = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append(" AND (g.first_name LIKE ? OR g.last_name LIKE ? OR cv.title LIKE ? OR cv.professional_summary LIKE ?)");
            String wildcard = "%" + query.trim() + "%";
            parameters.add(wildcard);
            parameters.add(wildcard);
            parameters.add(wildcard);
            parameters.add(wildcard);
        }

        if (careerId != null && !careerId.isBlank()) {
            sql.append(" AND c.career_id = ?");
            parameters.add(Integer.valueOf(careerId.trim()));
        }

        if (skill != null && !skill.isBlank()) {
            sql.append(" AND h.skill_name LIKE ?");
            parameters.add("%" + skill.trim() + "%");
        }

        if (city != null && !city.isBlank()) {
            sql.append(" AND g.city LIKE ?");
            parameters.add("%" + city.trim() + "%");
        }

        sql.append(" ORDER BY cv.updated_at DESC LIMIT ? OFFSET ?");
        parameters.add(pageSize);
        parameters.add((page - 1) * pageSize);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }
                    results.add(row);
                }
                return results;
            }
        }
    }

    /**
     * Agrega un CV a la lista de favoritos de la empresa.
     */
    public void favorite(int userId, int cvId) throws Exception {
        String sql = "INSERT IGNORE INTO favoritos(company_id, cv_id) " +
                     "SELECT company_id, ? FROM empresas WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cvId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    /**
     * Obtiene los candidatos guardados como favoritos por la empresa.
     */
    public List<Map<String, Object>> getFavorites(int userId) throws Exception {
        String sql = "SELECT f.favorite_id, f.created_at, cv.cv_id, cv.title, g.graduate_id, " +
                     "g.first_name, g.last_name, g.city, c.name AS career " +
                     "FROM favoritos f " +
                     "JOIN empresas emp ON emp.company_id = f.company_id " +
                     "JOIN cv ON cv.cv_id = f.cv_id " +
                     "JOIN egresados g ON g.graduate_id = cv.graduate_id " +
                     "LEFT JOIN carreras c ON c.career_id = g.career_id " +
                     "WHERE emp.user_id = ? " +
                     "ORDER BY f.created_at DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }
                    results.add(row);
                }
                return results;
            }
        }
    }

    /**
     * Elimina un candidato de los favoritos de la empresa.
     */
    public void removeFavorite(int userId, int favoriteId) throws Exception {
        String sql = "DELETE f FROM favoritos f " +
                     "JOIN empresas emp ON emp.company_id = f.company_id " +
                     "WHERE emp.user_id = ? AND f.favorite_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, favoriteId);
            statement.executeUpdate();
        }
    }

    /**
     * Registra una solicitud de contacto de la empresa hacia un egresado.
     */
    public void requestContact(int userId, int graduateId, String message) throws Exception {
        String sql = "INSERT INTO solicitudes_contacto(company_id, graduate_id, message) " +
                     "SELECT company_id, ?, ? FROM empresas WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, graduateId);
            statement.setString(2, message);
            statement.setInt(3, userId);
            statement.executeUpdate();
        }
    }

    /**
     * Obtiene las solicitudes de contacto emitidas por la empresa.
     */
    public List<Map<String, Object>> getContactRequests(int userId) throws Exception {
        String sql = "SELECT sc.request_id, sc.message, sc.status, sc.created_at, " +
                     "g.first_name, g.last_name, g.phone, u.email AS graduate_email, c.name AS career " +
                     "FROM solicitudes_contacto sc " +
                     "JOIN empresas emp ON emp.company_id = sc.company_id " +
                     "JOIN egresados g ON g.graduate_id = sc.graduate_id " +
                     "JOIN usuarios u ON u.user_id = g.user_id " +
                     "LEFT JOIN carreras c ON c.career_id = g.career_id " +
                     "WHERE emp.user_id = ? " +
                     "ORDER BY sc.created_at DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }
                    results.add(row);
                }
                return results;
            }
        }
    }

    /**
     * Registra la visualización del CV y actualiza el contador dentro de una transacción.
     */
    public void recordView(int userId, int cvId) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sqlInsert = "INSERT INTO historial_visualizaciones(company_id, cv_id) " +
                                  "SELECT company_id, ? FROM empresas WHERE user_id = ?";
                try (PreparedStatement insertStmt = connection.prepareStatement(sqlInsert)) {
                    insertStmt.setInt(1, cvId);
                    insertStmt.setInt(2, userId);
                    insertStmt.executeUpdate();
                }

                String sqlUpdate = "UPDATE cv SET views_count = views_count + 1 WHERE cv_id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(sqlUpdate)) {
                    updateStmt.setInt(1, cvId);
                    updateStmt.executeUpdate();
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
