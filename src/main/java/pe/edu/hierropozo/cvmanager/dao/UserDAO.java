package pe.edu.hierropozo.cvmanager.dao;

import java.sql.Connection;
import java.util.List;
import pe.edu.hierropozo.cvmanager.model.User;

/**
 * Interfaz DAO para operaciones de persistencia sobre la entidad 'usuarios'.
 */
public interface UserDAO {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param email Correo del usuario
     * @return Objeto User o null si no existe
     * @throws Exception si ocurre un error JDBC
     */
    User findByEmail(String email) throws Exception;

    /**
     * Busca un usuario por su ID primario.
     * @param userId ID del usuario
     * @return Objeto User o null si no existe
     * @throws Exception si ocurre un error JDBC
     */
    User findById(int userId) throws Exception;

    /**
     * Inserta un nuevo usuario dentro de una transacción activa (usando la conexión suministrada).
     * @param connection Conexión JDBC transaccional
     * @param user Objeto User con datos a insertar
     * @return ID autogenerado del usuario
     * @throws Exception si ocurre un error JDBC
     */
    int create(Connection connection, User user) throws Exception;

    /**
     * Lista usuarios con filtros opcionales de rol y estado.
     * @param role Rol del usuario (graduate, company, admin) o null para todos
     * @param status Estado de la cuenta (active, pending, suspended) o null para todos
     * @return Lista de usuarios coincidentes
     * @throws Exception si ocurre un error JDBC
     */
    List<User> findAll(String role, String status) throws Exception;

    /**
     * Actualiza el estado de una cuenta (no aplica sobre administradores).
     * @param userId ID del usuario
     * @param status Nuevo estado
     * @throws Exception si ocurre un error JDBC
     */
    void updateStatus(int userId, String status) throws Exception;

    /**
     * Registra la fecha y hora del último inicio de sesión exitoso.
     * @param userId ID del usuario
     * @throws Exception si ocurre un error JDBC
     */
    void touchLogin(int userId) throws Exception;

    /**
     * Verifica si ya existe al menos un usuario con rol 'admin' en el sistema.
     * @return true si existe al menos un administrador; false en caso contrario
     * @throws Exception si ocurre un error JDBC
     */
    boolean hasAdmin() throws Exception;
}
