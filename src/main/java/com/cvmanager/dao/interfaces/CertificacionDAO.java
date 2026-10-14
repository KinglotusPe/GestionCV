package com.cvmanager.dao.interfaces;
import com.cvmanager.models.Certificacion;
public interface CertificacionDAO {
 void save(int cvId,Integer id,Certificacion item)throws Exception;
}
