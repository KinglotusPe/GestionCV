package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.IdiomaDAO;
import com.cvmanager.models.Idioma;
/** Reutiliza el motor JDBC transaccional y restringe la sección a language. */
public class IdiomaDAOImpl implements IdiomaDAO {
 public void save(int cvId,Integer id,Idioma item)throws Exception {
  new CVSectionDAOImpl().save(cvId,"language",id,item.values());
 }
}
