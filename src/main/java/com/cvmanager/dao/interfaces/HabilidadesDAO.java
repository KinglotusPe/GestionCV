package com.cvmanager.dao.interfaces;
import com.cvmanager.models.Habilidades;
public interface HabilidadesDAO {
 void save(int cvId,Integer id,Habilidades item)throws Exception;
}
