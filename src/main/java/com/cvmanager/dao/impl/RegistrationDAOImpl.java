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
        if ("graduate".equals(role)) {
            Egresados g = new Egresados();
            g.setFirstName(firstName);
            g.setLastName(lastName);
            registerGraduate(email, passwordHash, g);
        } else {
            java.util.Map<String, String> data = new java.util.HashMap<>();
            data.put("companyName", companyName);
            registerCompany(email, passwordHash, data);
        }
    }

    public void registerGraduate(String email, String passwordHash, Egresados graduate) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                User user = new User();
                user.setEmail(email);
                user.setPasswordHash(passwordHash);
                user.setRole("graduate");
                user.setStatus("pending");

                int userId = userDAO.create(connection, user);
                graduate.setUserId(userId);
                int graduateId = graduateDAO.create(connection, graduate);

                String sqlCV = "INSERT INTO cv(graduate_id, title) VALUES(?, 'Mi Currículum Vitae')";
                try (PreparedStatement cvStmt = connection.prepareStatement(sqlCV)) {
                    cvStmt.setInt(1, graduateId);
                    cvStmt.executeUpdate();
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

    public void registerCompany(String email, String passwordHash, java.util.Map<String, String> data) throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                User user = new User();
                user.setEmail(email);
                user.setPasswordHash(passwordHash);
                user.setRole("company");
                user.setStatus("pending");

                int userId = userDAO.create(connection, user);

                String sql = "INSERT INTO empresas(user_id, company_name, trade_name, ruc, industry, phone, address, representative_name, representative_dni, representative_position) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement st = connection.prepareStatement(sql)) {
                    st.setInt(1, userId);
                    st.setString(2, data.get("companyName"));
                    st.setString(3, data.get("tradeName"));
                    st.setString(4, data.get("ruc"));
                    st.setString(5, data.get("industry"));
                    st.setString(6, data.get("phone"));
                    st.setString(7, data.get("address"));
                    st.setString(8, data.get("representativeName"));
                    st.setString(9, data.get("representativeDni"));
                    st.setString(10, data.get("representativePosition"));
                    st.executeUpdate();
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
