package com.cvmanager.dao.interfaces;
import com.cvmanager.models.Experiencia;
public interface ExperienciaDAO {
 void save(int cvId,Integer id,Experiencia item)throws Exception;
}
