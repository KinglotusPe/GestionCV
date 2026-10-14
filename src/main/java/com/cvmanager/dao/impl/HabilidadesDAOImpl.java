package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.HabilidadesDAO;
import com.cvmanager.models.Habilidades;
/** Reutiliza el motor JDBC transaccional y restringe la sección a skill. */
public class HabilidadesDAOImpl implements HabilidadesDAO {
 public void save(int cvId,Integer id,Habilidades item)throws Exception {
  new CVSectionDAOImpl().save(cvId,"skill",id,item.values());
 }
}
