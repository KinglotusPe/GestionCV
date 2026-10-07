package pe.edu.hierropozo.cvmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.model.Career;

/**
 * Implementación JDBC de la interfaz CareerDAO.
 * Realiza operaciones CRUD sobre la tabla 'carreras' usando PreparedStatement.
 */
public class CareerDAOImpl implements CareerDAO {

    /**
     * Mapea una fila de ResultSet a un objeto POJO Career.
     */
    private Career mapResultSetToCareer(ResultSet resultSet) throws SQLException {
        Career career = new Career();
        career.setCareerId(resultSet.getInt("career_id"));
        career.setName(resultSet.getString("name"));
        career.setCode(resultSet.getString("code"));
        career.setDescription(resultSet.getString("description"));
        career.setDurationYears(resultSet.getInt("duration_years"));
        career.setActive(resultSet.getBoolean("is_active"));
        return career;
    }

    @Override
    public List<Career> findAll(boolean onlyActive) throws Exception {
        String sql = "SELECT career_id, name, code, description, duration_years, is_active " +
                     "FROM carreras" +
                     (onlyActive ? " WHERE is_active = 1" : "") +
                     " ORDER BY name ASC";

        List<Career> list = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                list.add(mapResultSetToCareer(resultSet));
            }
        }
        return list;
    }

    @Override
    public Career findById(int careerId) throws Exception {
        String sql = "SELECT career_id, name, code, description, duration_years, is_active " +
                     "FROM carreras WHERE career_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, careerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCareer(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public void create(Career career) throws Exception {
        String sql = "INSERT INTO carreras(name, code, description, duration_years, is_active) " +
                     "VALUES(?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, career.getName());
            statement.setString(2, career.getCode());
            statement.setString(3, career.getDescription());
            statement.setInt(4, career.getDurationYears());
            statement.setBoolean(5, career.isActive());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Career career) throws Exception {
        String sql = "UPDATE carreras SET name = ?, code = ?, description = ?, " +
                     "duration_years = ?, is_active = ? WHERE career_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, career.getName());
            statement.setString(2, career.getCode());
            statement.setString(3, career.getDescription());
            statement.setInt(4, career.getDurationYears());
            statement.setBoolean(5, career.isActive());
            statement.setInt(6, career.getCareerId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int careerId) throws Exception {
        // Baja lógica: se desactiva la carrera para no romper integridad con egresados
        String sql = "UPDATE carreras SET is_active = 0 WHERE career_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, careerId);
            statement.executeUpdate();
        }
    }
}
