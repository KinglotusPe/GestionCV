package com.cvmanager.services;
import java.sql.SQLException;
import java.util.*;
import com.cvmanager.dao.interfaces.*;
import com.cvmanager.dao.impl.*;
import com.cvmanager.utils.ValidacionUtil;
public class JobService {
 private final ApplicationDAO dao=new ApplicationDAOImpl();
 public List<Map<String,Object>> list(int userId,boolean company,String query)throws SQLException{return new JobDAOImpl().list(userId,company?"company":"graduate",query,company?null:"ongoing",null);}
 public List<Map<String,Object>> applications(int userId,boolean company)throws SQLException{return dao.applications(userId,company);}
 public Map<String,Object> findOwn(int userId,int id)throws SQLException{return new JobDAOImpl().find(userId,"company",id);}
 public void save(int userId,Integer id,Map<String,String> p)throws SQLException{String career=p.get("careerId");new LaborPortalService().save(userId,"company",id,p,career==null?null:new String[]{career});}
 public void apply(int userId,int jobId,String message)throws SQLException{dao.apply(userId,jobId,ValidacionUtil.text(message,"Presentación",5000,true));}
 public void respond(int userId,boolean company,int id,String status)throws SQLException{
        if(status==null||!(company?Set.of("pending","reviewing","accepted","rejected"):Set.of("withdrawn")).contains(status))throw new IllegalArgumentException("Estado inválido.");
  dao.respond(userId,company,id,status);
 }
 public boolean canReadCv(int companyUserId,int cvId)throws SQLException{return dao.canReadCv(companyUserId,cvId);}
}
