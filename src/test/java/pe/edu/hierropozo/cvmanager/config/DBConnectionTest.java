package pe.edu.hierropozo.cvmanager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.hierropozo.cvmanager.dao.CareerDAO;
import pe.edu.hierropozo.cvmanager.dao.CareerDAOImpl;
import pe.edu.hierropozo.cvmanager.model.Career;

/**
 * Prueba de integración JDBC para verificar conectividad con MySQL 8
 * y persistencia en la tabla 'carreras'.
 */
@org.junit.jupiter.api.Tag("integration")
class DBConnectionTest {

    @Test
    @DisplayName("Debe conectarse exitosamente a MySQL usando DBConnection")
    void testConnection() throws Exception {
        try (Connection connection = DBConnection.getConnection()) {
            assertNotNull(connection, "La conexión no debe ser nula");
            assertFalse(connection.isClosed(), "La conexión debe estar activa");
        }
    }

    @Test
    @DisplayName("Debe recuperar las 4 carreras oficiales del Instituto Hierro Pozo")
    void testOfficialCareersLoaded() throws Exception {
        CareerDAO careerDAO = new CareerDAOImpl();
        List<Career> careers = careerDAO.findAll(true);

        assertNotNull(careers, "La lista de carreras no debe ser nula");
        assertTrue(careers.size() >= 4, "Deben existir al menos las 4 carreras oficiales iniciales");

        List<String> codes = careers.stream().map(Career::getCode).toList();
        assertTrue(codes.contains("ARC"), "Debe contener ARC (Administración de Redes y Comunicaciones)");
        assertTrue(codes.contains("EI"), "Debe contener EI (Electricidad Industrial)");
        assertTrue(codes.contains("GA"), "Debe contener GA (Gestión Administrativa)");
        assertTrue(codes.contains("MA"), "Debe contener MA (Mecatrónica Automotriz)");
    }
}
