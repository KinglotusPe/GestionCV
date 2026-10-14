package pe.edu.hierropozo.cvmanager.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.hierropozo.cvmanager.model.User;

/**
 * Prueba de integración para UserDAO.
 */
@org.junit.jupiter.api.Tag("integration")
class UserDAOTest {

    private final UserDAO userDAO = new UserDAOImpl();

    @Test
    @DisplayName("Debe buscar correctamente un usuario no existente devolviendo null")
    void testFindNonExistentUser() throws Exception {
        User user = userDAO.findByEmail("no_existe_999@hierropozo.edu.pe");
        assertNull(user, "El usuario inexistente debe ser null");
    }

    @Test
    @DisplayName("Debe listar usuarios sin arrojar excepciones")
    void testFindAllUsers() throws Exception {
        List<User> list = userDAO.findAll(null, null);
        assertNotNull(list, "La lista de usuarios nunca debe ser nula");
    }
}
