package pe.edu.hierropozo.cvmanager.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.model.Curriculum;

/**
 * Implementación JDBC de CurriculumDAO.
 * Gestiona el CV del egresado y sus colecciones hijas asociadas.
 */
public class CurriculumDAOImpl implements CurriculumDAO {

    /**
     * Mapea una fila del ResultSet a un objeto POJO Curriculum.
     */
    private Curriculum mapResultSetToCurriculum(ResultSet resultSet) throws SQLException {
        Curriculum curriculum = new Curriculum();
        curriculum.setCvId(resultSet.getInt("cv_id"));
        curriculum.setGraduateId(resultSet.getInt("graduate_id"));
        curriculum.setTitle(resultSet.getString("title"));
        curriculum.setProfessionalSummary(resultSet.getString("professional_summary"));
        curriculum.setCvPdfUrl(resultSet.getString("cv_pdf_url"));
        curriculum.setPublished(resultSet.getBoolean("is_published"));
        curriculum.setViewsCount(resultSet.getInt("views_count"));
        return curriculum;
    }

    @Override
    public Curriculum findByGraduate(int graduateId) throws Exception {
        String sql = "SELECT cv_id, graduate_id, title, professional_summary, cv_pdf_url, " +
                     "is_published, views_count FROM cv WHERE graduate_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, graduateId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCurriculum(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public Curriculum findPublished(int cvId) throws Exception {
        String sql = "SELECT cv_id, graduate_id, title, professional_summary, cv_pdf_url, " +
                     "is_published, views_count FROM cv WHERE cv_id = ? AND is_published = 1 AND EXISTS (SELECT 1 FROM egresados g JOIN usuarios u ON u.user_id = g.user_id WHERE g.graduate_id = cv.graduate_id AND g.is_public = 1 AND u.status = 'active' AND u.role = 'graduate')";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cvId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCurriculum(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public void updateMain(Curriculum curriculum) throws Exception {
        String sql = "UPDATE cv SET title = ?, professional_summary = ? WHERE cv_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, curriculum.getTitle());
            statement.setString(2, curriculum.getProfessionalSummary());
            statement.setInt(3, curriculum.getCvId());
            statement.executeUpdate();
        }
    }

    @Override
    public void setPublished(int cvId, boolean published) throws Exception {
        String sql = "UPDATE cv SET is_published = ? WHERE cv_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBoolean(1, published);
            statement.setInt(2, cvId);
            statement.executeUpdate();
        }
    }

    @Override
    public List<Map<String, Object>> section(String tableName, String foreignKeyColumn, int cvId) throws Exception {
        // Validación de lista blanca para prevenir inyección en nombres de tablas dinámicas de secciones
        List<String> allowedTables = List.of("educacion", "experiencia", "habilidades", "idiomas", "certificaciones", "referencias");
        if (!allowedTables.contains(tableName) || !"cv_id".equals(foreignKeyColumn)) {
            throw new IllegalArgumentException("Nombre de sección no permitido: " + tableName);
        }

        String sql = "SELECT * FROM " + tableName + " WHERE " + foreignKeyColumn + " = ?";
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cvId);
            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }
                    results.add(row);
                }
            }
        }
        return results;
    }

    @Override
    public void addEducation(int cvId, Map<String, String> params) throws Exception {
        String sql = "INSERT INTO educacion(cv_id, institution, degree, field_of_study, start_date, end_date, description) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cvId);
            statement.setString(2, params.getOrDefault("institution", ""));
            statement.setString(3, params.getOrDefault("degree", ""));
            statement.setString(4, params.getOrDefault("fieldOfStudy", params.getOrDefault("field", "")));
            statement.setDate(5, parseDate(params.getOrDefault("startDate", params.get("start"))));
            statement.setDate(6, parseDate(params.getOrDefault("endDate", params.get("end"))));
            statement.setString(7, params.getOrDefault("description", ""));
            statement.executeUpdate();
        }
    }

    @Override
    public void addExperience(int cvId, Map<String, String> params) throws Exception {
        String sql = "INSERT INTO experiencia(cv_id, company_name, position, start_date, end_date, responsibilities) " +
                     "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cvId);
            statement.setString(2, params.getOrDefault("companyName", params.getOrDefault("company", "")));
            statement.setString(3, params.getOrDefault("position", ""));
            statement.setDate(4, parseDate(params.getOrDefault("startDate", params.get("start"))));
            statement.setDate(5, parseDate(params.getOrDefault("endDate", params.get("end"))));
            statement.setString(6, params.getOrDefault("responsibilities", ""));
            statement.executeUpdate();
        }
    }

    @Override
    public void addSkill(int cvId, Map<String, String> params) throws Exception {
        String sql = "INSERT INTO habilidades(cv_id, skill_name, skill_category, proficiency_level) " +
                     "VALUES(?, ?, ?, ?)";

        int level = 3;
        try {
            level = Integer.parseInt(params.getOrDefault("proficiencyLevel", params.getOrDefault("level", "3")));
        } catch (NumberFormatException ignored) {
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cvId);
            statement.setString(2, params.getOrDefault("skillName", params.getOrDefault("name", "")));
            statement.setString(3, params.getOrDefault("category", "technical"));
            statement.setInt(4, level);
            statement.executeUpdate();
        }
    }

    @Override
    public void addLanguage(int cvId, Map<String, String> params) throws Exception {
        String sql = "INSERT INTO idiomas(cv_id, language_name, proficiency_level) VALUES(?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cvId);
            statement.setString(2, params.getOrDefault("languageName", params.getOrDefault("name", "")));
            statement.setString(3, params.getOrDefault("proficiencyLevel", params.getOrDefault("level", "basic")));
            statement.executeUpdate();
        }
    }

    @Override
    public void addCertification(int cvId, Map<String, String> params) throws Exception {
        String sql = "INSERT INTO certificaciones(cv_id, name, issuing_organization, issue_date) VALUES(?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cvId);
            statement.setString(2, params.getOrDefault("name", ""));
            statement.setString(3, params.getOrDefault("issuingOrganization", params.getOrDefault("issuer", params.getOrDefault("organization", ""))));
            statement.setDate(4, parseDate(params.getOrDefault("issueDate", params.get("date"))));
            statement.executeUpdate();
        }
    }

    @Override
    public void addReference(int cvId, Map<String, String> params) throws Exception {
        String sql = "INSERT INTO referencias(cv_id, full_name, position, company_name, phone, email) " +
                     "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cvId);
            statement.setString(2, params.getOrDefault("fullName", params.getOrDefault("name", "")));
            statement.setString(3, params.getOrDefault("position", ""));
            statement.setString(4, params.getOrDefault("companyName", params.getOrDefault("company", "")));
            statement.setString(5, params.getOrDefault("phone", ""));
            statement.setString(6, params.getOrDefault("email", ""));
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteSection(String sectionType, int itemId, int cvId) throws Exception {
        Map<String, String[]> tableMap = Map.of(
            "education", new String[]{"educacion", "education_id"},
            "experience", new String[]{"experiencia", "experience_id"},
            "skill", new String[]{"habilidades", "skill_id"},
            "language", new String[]{"idiomas", "language_id"},
            "certification", new String[]{"certificaciones", "certification_id"},
            "reference", new String[]{"referencias", "reference_id"}
        );

        String[] target = tableMap.get(sectionType);
        if (target == null) {
            throw new IllegalArgumentException("Tipo de sección inválido: " + sectionType);
        }

        String sql = "DELETE FROM " + target[0] + " WHERE " + target[1] + " = ? AND cv_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, itemId);
            statement.setInt(2, cvId);
            statement.executeUpdate();
        }
    }

    private Date parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return Date.valueOf(dateString.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
