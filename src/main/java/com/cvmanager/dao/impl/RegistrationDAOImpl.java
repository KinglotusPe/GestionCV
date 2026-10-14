package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.cvmanager.utils.DBConnection;
import com.cvmanager.dao.impl.EgresadoDAOImpl;
import com.cvmanager.dao.impl.UsuarioDAOImpl;
import com.cvmanager.models.Egresados;
import com.cvmanager.models.User;
import com.cvmanager.utils.PasswordUtil;

/**
 * Servicio transaccional para registro de nuevos usuarios (egresados, empresas y administradores).
 * Aplica transacciones JDBC manuales para garantizar consistencia atómica.
 */
public class RegistrationDAOImpl implements RegistrationDAO {

    private final UsuarioDAOImpl userDAO = new UsuarioDAOImpl();
    private final EgresadoDAOImpl graduateDAO = new EgresadoDAOImpl();

    /**
     * Registra un egresado o empresa de forma transaccional.
     * Si alguna operación falla, se ejecuta rollback.
     */
    public void register(String email, String passwordHash, String role, String firstName, String lastName, String companyName) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // 1. Crear usuario base
                User user = new User();
                user.setEmail(email);
                user.setPasswordHash(passwordHash);
                user.setRole(role);
                user.setStatus("pending"); // Requiere activación inicial

                int userId = userDAO.create(connection, user);

                // 2. Crear perfil dependiente
                if ("graduate".equals(role)) {
                    Egresados graduate = new Egresados();
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
    public void createAdmin(String email, String passwordHash) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement lock=connection.prepareStatement("SELECT GET_LOCK('cvmanager_initial_admin',10)"); java.sql.ResultSet rs=lock.executeQuery()) {
                if(!rs.next() || rs.getInt(1)!=1)throw new IllegalStateException("La configuración está en curso. Intente nuevamente.");
            }
            try {
                try(PreparedStatement st=connection.prepareStatement("SELECT 1 FROM usuarios WHERE role='admin' LIMIT 1"); java.sql.ResultSet rs=st.executeQuery()) {
                    if(rs.next())throw new IllegalArgumentException("Ya existe un administrador.");
                }
                User admin=new User();admin.setEmail(email);admin.setPasswordHash(passwordHash);admin.setRole("admin");admin.setStatus("active");userDAO.create(connection,admin);
            } finally {
                try(PreparedStatement unlock=connection.prepareStatement("SELECT RELEASE_LOCK('cvmanager_initial_admin')")){unlock.execute();}
            }
        }
    }
}
