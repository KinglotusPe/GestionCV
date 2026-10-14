package com.cvmanager.dao.interfaces;

import java.util.List;
import java.util.Map;
import com.cvmanager.models.CV;

/**
 * Interfaz DAO para operaciones de persistencia sobre el CV y sus secciones
 * (educación, experiencia, habilidades, idiomas, certificaciones, referencias).
 */
public interface CVDAO {

    /**
     * Obtiene el CV principal perteneciente a un egresado.
     * @param graduateId ID del egresado
     * @return Objeto CV o null
     * @throws Exception si ocurre un error JDBC
     */
    CV findByGraduate(int graduateId) throws Exception;

    /**
     * Obtiene un CV publicado por su identificador primario.
     * @param cvId ID del CV
     * @return Objeto CV si está publicado; null si no existe o no está publicado
     * @throws Exception si ocurre un error JDBC
     */
    CV findPublished(int cvId) throws Exception;

    /**
     * Actualiza la información básica del CV (título y resumen profesional).
     * @param curriculum Objeto CV con nuevos datos
     * @throws Exception si ocurre un error JDBC
     */
    void updateMain(CV curriculum) throws Exception;

    /**
     * Cambia el estado de publicación del CV (is_published = true/false).
     * @param cvId ID del CV
     * @param published true para publicar; false para despublicar
     * @throws Exception si ocurre un error JDBC
     */
    void setPublished(int cvId, boolean published) throws Exception;

    /**
     * Lista los registros dinámicos de una sección del CV (educacion, experiencia, etc.).
     * @param tableName Nombre de la tabla de la sección
     * @param foreignKeyColumn Nombre de la columna de enlace (ej: 'cv_id')
     * @param cvId ID del CV
     * @return Lista de mapas con pares columna-valor
     * @throws Exception si ocurre un error JDBC
     */
    List<Map<String, Object>> section(String tableName, String foreignKeyColumn, int cvId) throws Exception;

    /**
     * Agrega un registro a la sección de educación.
     * @param cvId ID del CV
     * @param params Mapa con parámetros del formulario
     * @throws Exception si ocurre un error JDBC
     */
    void addEducation(int cvId, Map<String, String> params) throws Exception;

    /**
     * Agrega un registro a la sección de experiencia laboral.
     * @param cvId ID del CV
     * @param params Mapa con parámetros del formulario
     * @throws Exception si ocurre un error JDBC
     */
    void addExperience(int cvId, Map<String, String> params) throws Exception;

    /**
     * Agrega un registro a la sección de habilidades.
     * @param cvId ID del CV
     * @param params Mapa con parámetros del formulario
     * @throws Exception si ocurre un error JDBC
     */
    void addSkill(int cvId, Map<String, String> params) throws Exception;

    /**
     * Agrega un registro a la sección de idiomas.
     * @param cvId ID del CV
     * @param params Mapa con parámetros del formulario
     * @throws Exception si ocurre un error JDBC
     */
    void addLanguage(int cvId, Map<String, String> params) throws Exception;

    /**
     * Agrega un registro a la sección de certificaciones.
     * @param cvId ID del CV
     * @param params Mapa con parámetros del formulario
     * @throws Exception si ocurre un error JDBC
     */
    void addCertification(int cvId, Map<String, String> params) throws Exception;

    /**
     * Agrega una referencia profesional de contacto.
     * @param cvId ID del CV
     * @param params Mapa con parámetros del formulario
     * @throws Exception si ocurre un error JDBC
     */
    void addReference(int cvId, Map<String, String> params) throws Exception;

    /**
     * Elimina un elemento de una sección específica del CV asegurando la pertenencia al cv_id.
     * @param sectionType Nombre del tipo de sección ('educacion', 'experiencia', 'reference', etc.)
     * @param itemId ID del registro a eliminar
     * @param cvId ID del CV para verificación de pertenencia
     * @throws Exception si ocurre un error JDBC
     */
    void deleteSection(String sectionType, int itemId, int cvId) throws Exception;
}
