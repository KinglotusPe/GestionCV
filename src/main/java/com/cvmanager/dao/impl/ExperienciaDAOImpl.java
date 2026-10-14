package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.ExperienciaDAO;
import com.cvmanager.models.Experiencia;
/** Reutiliza el motor JDBC transaccional y restringe la sección a experience. */
public class ExperienciaDAOImpl implements ExperienciaDAO {
 public void save(int cvId,Integer id,Experiencia item)throws Exception {
  new CVSectionDAOImpl().save(cvId,"experience",id,item.values());
 }
}
