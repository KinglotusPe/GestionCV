package com.cvmanager.dao.interfaces;

import java.sql.Connection;
import com.cvmanager.models.Egresados;

/**
 * Interfaz DAO para operaciones de persistencia sobre la entidad 'egresados'.
 */
public interface EgresadosDAO {

    /**
     * Registra un egresado dentro de una transacción JDBC activa.
     * @param connection Conexión transaccional
     * @param graduate Objeto Egresados con datos básicos
     * @return ID autogenerado del egresado (graduate_id)
     * @throws Exception si ocurre un error JDBC
     */
    int create(Connection connection, Egresados graduate) throws Exception;

    /**
     * Obtiene el perfil de un egresado por el ID del usuario al que pertenece.
     * @param userId ID del usuario en la tabla 'usuarios'
     * @return Objeto Egresados o null si no se encuentra
     * @throws Exception si ocurre un error JDBC
     */
    Egresados findByUserId(int userId) throws Exception;

    /**
     * Obtiene el perfil de un egresado por su identificador primario (graduate_id).
     * @param graduateId ID del egresado
     * @return Objeto Egresados o null si no se encuentra
     * @throws Exception si ocurre un error JDBC
     */
    Egresados findById(int graduateId) throws Exception;

    /**
     * Actualiza la información del perfil del egresado.
     * @param graduate Objeto Egresados con los datos modificados
     * @throws Exception si ocurre un error JDBC
     */
    void update(Egresados graduate) throws Exception;
}
