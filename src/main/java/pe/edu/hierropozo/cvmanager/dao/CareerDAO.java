package pe.edu.hierropozo.cvmanager.dao;

import java.util.List;
import pe.edu.hierropozo.cvmanager.model.Career;

/**
 * Interfaz DAO para operaciones de persistencia sobre la entidad 'carreras'.
 */
public interface CareerDAO {

    /**
     * Lista todas las carreras registradas.
     * @param onlyActive true si solo se desean las carreras activas; false para todas.
     * @return Lista de objetos Career
     * @throws Exception si ocurre un error en la consulta JDBC
     */
    List<Career> findAll(boolean onlyActive) throws Exception;

    /**
     * Busca una carrera por su identificador primario.
     * @param careerId ID de la carrera
     * @return Objeto Career o null si no se encuentra
     * @throws Exception si ocurre un error JDBC
     */
    Career findById(int careerId) throws Exception;

    /**
     * Registra una nueva carrera en la base de datos.
     * @param career Entidad Career con los datos a insertar
     * @throws Exception si ocurre un error JDBC
     */
    void create(Career career) throws Exception;

    /**
     * Actualiza la información de una carrera existente.
     * @param career Entidad Career con datos actualizados
     * @throws Exception si ocurre un error JDBC
     */
    void update(Career career) throws Exception;

    /**
     * Realiza una baja lógica desactivando la carrera (is_active = false).
     * @param careerId ID de la carrera a desactivar
     * @throws Exception si ocurre un error JDBC
     */
    void delete(int careerId) throws Exception;
}
