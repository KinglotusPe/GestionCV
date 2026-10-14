package pe.edu.hierropozo.cvmanager.service;
import java.util.*;
import pe.edu.hierropozo.cvmanager.dao.AdminDAO;
public class AdminService {
 private final AdminDAO dao=new AdminDAO();
 public Map<String,Integer> stats()throws Exception{return dao.stats();}
 public List<Map<String,Object>> byCareer()throws Exception{return dao.byCareer();}
 public List<Map<String,Object>> getAuditLogs(int limit)throws Exception{return dao.getAuditLogs(Math.max(1,Math.min(limit,1000)));}
}
