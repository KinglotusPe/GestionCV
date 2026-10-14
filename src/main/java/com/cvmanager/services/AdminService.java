package com.cvmanager.services;
import java.util.*;
import com.cvmanager.dao.interfaces.AdminDAO;
import com.cvmanager.dao.impl.AdminDAOImpl;
public class AdminService {
 private final AdminDAO dao=new AdminDAOImpl();
 public Map<String,Integer> stats()throws Exception{return dao.stats();}
 public List<Map<String,Object>> byCareer()throws Exception{return dao.byCareer();}
 public List<Map<String,Object>> getAuditLogs(int limit)throws Exception{return dao.getAuditLogs(Math.max(1,Math.min(limit,1000)));}
}
