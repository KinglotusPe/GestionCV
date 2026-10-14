package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.EducaciónDAO;
import com.cvmanager.models.Educación;
/** Reutiliza el motor JDBC transaccional y restringe la sección a education. */
public class EducaciónDAOImpl implements EducaciónDAO {
 public void save(int cvId,Integer id,Educación item)throws Exception {
  new CVSectionDAOImpl().save(cvId,"education",id,item.values());
 }
}
