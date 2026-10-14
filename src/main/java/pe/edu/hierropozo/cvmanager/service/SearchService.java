package pe.edu.hierropozo.cvmanager.service;
import java.util.*;
import pe.edu.hierropozo.cvmanager.dao.SearchDAO;
import pe.edu.hierropozo.cvmanager.util.ValidationUtil;
public class SearchService {
 private final SearchDAO dao=new SearchDAO();
 public List<Map<String,Object>> search(String query,String careerId,String skill,String city,String year,int page,int pageSize)throws Exception {
  if(year!=null&&!year.isBlank())year=String.valueOf(ValidationUtil.integer(year,"Año de graduación",1950,2100));
  if(careerId!=null&&!careerId.isBlank())careerId=String.valueOf(ValidationUtil.integer(careerId,"Carrera",1,Integer.MAX_VALUE));
  return dao.search(query,careerId,skill,city,year,Math.max(1,page),Math.max(1,Math.min(pageSize,100)));
 }
 public void favorite(int userId,int cvId)throws Exception{dao.favorite(userId,cvId);}
 public void removeFavorite(int userId,int favoriteId)throws Exception{dao.removeFavorite(userId,favoriteId);}
 public List<Map<String,Object>> getFavorites(int userId)throws Exception{return dao.getFavorites(userId);}
 public void requestContact(int userId,int graduateId,String message)throws Exception{dao.requestContact(userId,graduateId,ValidationUtil.text(message,"Mensaje",5000,true));}
 public List<Map<String,Object>> getContactRequests(int userId)throws Exception{return dao.getContactRequests(userId);}
 public void recordView(int userId,int cvId)throws Exception{dao.recordView(userId,cvId);}
}
