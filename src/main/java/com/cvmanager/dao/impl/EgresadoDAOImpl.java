package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.models.Egresados;

/**
 * Implementación JDBC de la interfaz EgresadosDAO.
 * Gestiona la persistencia de perfiles de egresados vinculados con la tabla 'carreras'.
 */
public class EgresadoDAOImpl implements EgresadosDAO {

    /**
     * Mapea una fila del ResultSet a un objeto POJO Egresados.
     */
    private Egresados mapResultSetToGraduate(ResultSet resultSet) throws SQLException {
        Egresados graduate = new Egresados();
        graduate.setGraduateId(resultSet.getInt("graduate_id"));
        graduate.setUserId(resultSet.getInt("user_id"));
        graduate.setFirstName(resultSet.getString("first_name"));
        graduate.setLastName(resultSet.getString("last_name"));
        graduate.setDocumentType(resultSet.getString("document_type"));
        graduate.setDocumentNumber(resultSet.getString("document_number"));
        graduate.setPhone(resultSet.getString("phone"));
        graduate.setAddress(resultSet.getString("address"));
        graduate.setCity(resultSet.getString("city"));
        graduate.setCountry(resultSet.getString("country"));
        graduate.setBirthDate(resultSet.getDate("birth_date"));
        graduate.setPhotoUrl(resultSet.getString("photo_url"));
        graduate.setLinkedinUrl(resultSet.getString("linkedinUrl"));
        graduate.setPortfolioUrl(resultSet.getString("portfolioUrl"));

        int careerId = resultSet.getInt("career_id");
        graduate.setCareerId(resultSet.wasNull() ? null : careerId);

        int graduationYear = resultSet.getInt("graduation_year");
        graduate.setGraduationYear(resultSet.wasNull() ? null : graduationYear);

        graduate.setPublicProfile(resultSet.getBoolean("is_public"));

        try {
            graduate.setInstitutionalCode(resultSet.getString("institutional_code"));
        } catch (SQLException ignored) {}
        try {
            int entryYear = resultSet.getInt("entry_year");
            graduate.setEntryYear(resultSet.wasNull() ? null : entryYear);
        } catch (SQLException ignored) {}
        try {
            String academicStatus = resultSet.getString("academic_status");
            graduate.setAcademicStatus(academicStatus != null ? academicStatus : "EGRESADO");
        } catch (SQLException ignored) {}

        // Se captura el nombre de la carrera si viene del JOIN
        try {
            graduate.setCareerName(resultSet.getString("career_name"));
        } catch (SQLException ignored) {
            // Columna no presente en la proyección de la consulta
        }

        return graduate;
    }

    @Override
    public int create(Connection connection, Egresados graduate) throws Exception {
        String sql = "INSERT INTO egresados(user_id, first_name, last_name, document_type, document_number, institutional_code, phone, career_id, entry_year, graduation_year, academic_status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, graduate.getUserId());
            statement.setString(2, graduate.getFirstName());
            statement.setString(3, graduate.getLastName());
            statement.setString(4, graduate.getDocumentType() != null ? graduate.getDocumentType() : "DNI");
            statement.setString(5, graduate.getDocumentNumber());
            statement.setString(6, graduate.getInstitutionalCode());
            statement.setString(7, graduate.getPhone());
            if (graduate.getCareerId() != null) statement.setInt(8, graduate.getCareerId()); else statement.setNull(8, Types.INTEGER);
            if (graduate.getEntryYear() != null) statement.setInt(9, graduate.getEntryYear()); else statement.setNull(9, Types.SMALLINT);
            if (graduate.getGraduationYear() != null) statement.setInt(10, graduate.getGraduationYear()); else statement.setNull(10, Types.SMALLINT);
            statement.setString(11, graduate.getAcademicStatus() != null ? graduate.getAcademicStatus() : "EGRESADO");
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo generar el ID del egresado.");
    }

    @Override
    public Egresados findByUserId(int userId) throws Exception {
        String sql = "SELECT g.graduate_id, g.user_id, g.first_name, g.last_name, g.document_type, " +
                     "g.document_number, g.phone, g.address, g.city, g.country, g.birth_date, " +
                     "g.photo_url, g.linkedin_url AS linkedinUrl, g.portfolio_url AS portfolioUrl, " +
                     "g.career_id, g.graduation_year, g.is_public, c.name AS career_name " +
                     "FROM egresados g " +
                     "LEFT JOIN carreras c ON c.career_id = g.career_id " +
                     "WHERE g.user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToGraduate(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public Egresados findById(int graduateId) throws Exception {
        String sql = "SELECT g.graduate_id, g.user_id, g.first_name, g.last_name, g.document_type, " +
                     "g.document_number, g.phone, g.address, g.city, g.country, g.birth_date, " +
                     "g.photo_url, g.linkedin_url AS linkedinUrl, g.portfolio_url AS portfolioUrl, " +
                     "g.career_id, g.graduation_year, g.is_public, c.name AS career_name " +
                     "FROM egresados g " +
                     "LEFT JOIN carreras c ON c.career_id = g.career_id " +
                     "WHERE g.graduate_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, graduateId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToGraduate(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public void update(Egresados graduate) throws Exception {
        String sql = "UPDATE egresados SET first_name = ?, last_name = ?, document_type = ?, " +
                     "document_number = ?, institutional_code = ?, phone = ?, address = ?, city = ?, country = ?, " +
                     "birth_date = ?, linkedin_url = ?, portfolio_url = ?, career_id = ?, " +
                     "entry_year = ?, graduation_year = ?, academic_status = ?, is_public = ? WHERE graduate_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, graduate.getFirstName());
            statement.setString(2, graduate.getLastName());
            statement.setString(3, graduate.getDocumentType());
            statement.setString(4, graduate.getDocumentNumber());
            statement.setString(5, graduate.getInstitutionalCode());
            statement.setString(6, graduate.getPhone());
            statement.setString(7, graduate.getAddress());
            statement.setString(8, graduate.getCity());
            statement.setString(9, graduate.getCountry());

            if (graduate.getBirthDate() != null) {
                statement.setDate(10, graduate.getBirthDate());
            } else {
                statement.setNull(10, Types.DATE);
            }

            statement.setString(11, graduate.getLinkedinUrl());
            statement.setString(12, graduate.getPortfolioUrl());

            if (graduate.getCareerId() != null) {
                statement.setInt(13, graduate.getCareerId());
            } else {
                statement.setNull(13, Types.INTEGER);
            }

            if (graduate.getEntryYear() != null) {
                statement.setInt(14, graduate.getEntryYear());
            } else {
                statement.setNull(14, Types.SMALLINT);
            }

            if (graduate.getGraduationYear() != null) {
                statement.setInt(15, graduate.getGraduationYear());
            } else {
                statement.setNull(15, Types.SMALLINT);
            }

            statement.setString(16, graduate.getAcademicStatus() != null ? graduate.getAcademicStatus() : "EGRESADO");
            statement.setBoolean(17, graduate.isPublicProfile());
            statement.setInt(18, graduate.getGraduateId());
            statement.executeUpdate();
        }
    }
}
