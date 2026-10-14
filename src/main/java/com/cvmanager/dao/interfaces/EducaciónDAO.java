package com.cvmanager.dao.interfaces;
import com.cvmanager.models.Educación;
public interface EducaciónDAO {
 void save(int cvId,Integer id,Educación item)throws Exception;
}
