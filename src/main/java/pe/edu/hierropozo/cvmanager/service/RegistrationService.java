package pe.edu.hierropozo.cvmanager.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import pe.edu.hierropozo.cvmanager.config.DBConnection;
import pe.edu.hierropozo.cvmanager.dao.GraduateDAOImpl;
import pe.edu.hierropozo.cvmanager.dao.UserDAOImpl;
import pe.edu.hierropozo.cvmanager.model.Graduate;
import pe.edu.hierropozo.cvmanager.model.User;
import pe.edu.hierropozo.cvmanager.util.PasswordUtil;

/**
 * Servicio transaccional para registro de nuevos usuarios (egresados, empresas y administradores).
 * Aplica transacciones JDBC manuales para garantizar consistencia atómica.
 */
public class RegistrationService {

    private final UserDAOImpl userDAO = new UserDAOImpl();
    private final GraduateDAOImpl graduateDAO = new GraduateDAOImpl();

    /**
     * Registra un egresado o empresa de forma transaccional.
     * Si alguna operación falla, se ejecuta rollback.
     */
    public void register(String email, String password, String role, String firstName, String lastName, String companyName) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // 1. Crear usuario base
                User user = new User();
                user.setEmail(email);
                user.setPasswordHash(PasswordUtil.hash(password));
                user.setRole(role);
                user.setStatus("pending"); // Requiere activación inicial

                int userId = userDAO.create(connection, user);

                // 2. Crear perfil dependiente
                if ("graduate".equals(role)) {
                    Graduate graduate = new Graduate();
                    graduate.setUserId(userId);
                    graduate.setFirstName(firstName);
                    graduate.setLastName(lastName);
                    int graduateId = graduateDAO.create(connection, graduate);

                    // Inicializar cabecera del CV para el nuevo egresado
                    String sqlCV = "INSERT INTO cv(graduate_id, title) VALUES(?, 'Mi Currículum Vitae')";
                    try (PreparedStatement cvStmt = connection.prepareStatement(sqlCV)) {
                        cvStmt.setInt(1, graduateId);
                        cvStmt.executeUpdate();
                    }
                } else {
                    // Perfil de empresa
                    String sqlCompany = "INSERT INTO empresas(user_id, company_name) VALUES(?, ?)";
                    try (PreparedStatement companyStmt = connection.prepareStatement(sqlCompany)) {
                        companyStmt.setInt(1, userId);
                        companyStmt.setString(2, companyName);
                        companyStmt.executeUpdate();
                    }
                }

                // Confirmar transacción
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * Registra el administrador inicial del sistema.
     */
    public void createAdmin(String email, String password) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            User admin = new User();
            admin.setEmail(email);
            admin.setPasswordHash(PasswordUtil.hash(password));
            admin.setRole("admin");
            admin.setStatus("active");
            userDAO.create(connection, admin);
        }
    }
}
