package pe.edu.hierropozo.cvmanager.service;
import java.sql.SQLException;
import java.util.*;
import pe.edu.hierropozo.cvmanager.dao.*;
import pe.edu.hierropozo.cvmanager.util.ValidationUtil;
public class JobService {
 private final ApplicationDAO dao=new ApplicationDAO();
 public List<Map<String,Object>> list(int userId,boolean company,String query)throws SQLException{return new JobDAO().list(userId,company?"company":"graduate",query,company?null:"ongoing",null);}
 public List<Map<String,Object>> applications(int userId,boolean company)throws SQLException{return dao.applications(userId,company);}
 public Map<String,Object> findOwn(int userId,int id)throws SQLException{return new JobDAO().find(userId,"company",id);}
 public void save(int userId,Integer id,Map<String,String> p)throws SQLException{String career=p.get("careerId");new LaborPortalService().save(userId,"company",id,p,career==null?null:new String[]{career});}
 public void apply(int userId,int jobId,String message)throws SQLException{dao.apply(userId,jobId,ValidationUtil.text(message,"Presentación",5000,true));}
 public void respond(int userId,boolean company,int id,String status)throws SQLException{
        if(status==null||!(company?Set.of("pending","reviewing","accepted","rejected"):Set.of("withdrawn")).contains(status))throw new IllegalArgumentException("Estado inválido.");
  dao.respond(userId,company,id,status);
 }
 public boolean canReadCv(int companyUserId,int cvId)throws SQLException{return dao.canReadCv(companyUserId,cvId);}
}
