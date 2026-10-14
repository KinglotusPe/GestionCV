package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.CertificacionDAO;
import com.cvmanager.models.Certificacion;
/** Reutiliza el motor JDBC transaccional y restringe la sección a certification. */
public class CertificacionDAOImpl implements CertificacionDAO {
 public void save(int cvId,Integer id,Certificacion item)throws Exception {
  new CVSectionDAOImpl().save(cvId,"certification",id,item.values());
 }
}
