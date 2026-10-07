package pe.edu.hierropozo.cvmanager.dao;

import java.sql.Connection;
import pe.edu.hierropozo.cvmanager.model.Graduate;

/**
 * Interfaz DAO para operaciones de persistencia sobre la entidad 'egresados'.
 */
public interface GraduateDAO {

    /**
     * Registra un egresado dentro de una transacción JDBC activa.
     * @param connection Conexión transaccional
     * @param graduate Objeto Graduate con datos básicos
     * @return ID autogenerado del egresado (graduate_id)
     * @throws Exception si ocurre un error JDBC
     */
    int create(Connection connection, Graduate graduate) throws Exception;

    /**
     * Obtiene el perfil de un egresado por el ID del usuario al que pertenece.
     * @param userId ID del usuario en la tabla 'usuarios'
     * @return Objeto Graduate o null si no se encuentra
     * @throws Exception si ocurre un error JDBC
     */
    Graduate findByUserId(int userId) throws Exception;

    /**
     * Obtiene el perfil de un egresado por su identificador primario (graduate_id).
     * @param graduateId ID del egresado
     * @return Objeto Graduate o null si no se encuentra
     * @throws Exception si ocurre un error JDBC
     */
    Graduate findById(int graduateId) throws Exception;

    /**
     * Actualiza la información del perfil del egresado.
     * @param graduate Objeto Graduate con los datos modificados
     * @throws Exception si ocurre un error JDBC
     */
    void update(Graduate graduate) throws Exception;
}
