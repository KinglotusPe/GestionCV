package pe.edu.hierropozo.cvmanager.dao;
import java.sql.*;
import java.util.*;
import pe.edu.hierropozo.cvmanager.config.DBConnection;

/** Consultas de perfiles, consentimiento de contacto y referencias a archivos. */
public class ProfileDAO {
 public Map<String,Object> company(int userId)throws SQLException {
  var rows=JobDAO.rows("SELECT company_name,ruc,industry,company_size,phone,website,description FROM empresas WHERE user_id=?",userId);
  return rows.isEmpty()?null:rows.get(0);
 }
 public void saveCompany(int userId,Map<String,String> values)throws SQLException {
  try(Connection c=DBConnection.getConnection()){
   if(JobDAO.update(c,"UPDATE empresas SET company_name=?,ruc=?,industry=?,company_size=?,phone=?,website=?,description=? WHERE user_id=?",values.get("companyName"),values.get("ruc"),values.get("industry"),values.get("companySize"),values.get("phone"),values.get("website"),values.get("description"),userId)!=1)throw new IllegalArgumentException("Empresa inexistente.");
  }
 }
 public List<Map<String,Object>> requests(int userId)throws SQLException {
  return JobDAO.rows("SELECT s.request_id,s.message,s.status,s.created_at,e.company_name,e.phone,u.email FROM solicitudes_contacto s JOIN egresados g ON g.graduate_id=s.graduate_id JOIN empresas e ON e.company_id=s.company_id JOIN usuarios u ON u.user_id=e.user_id WHERE g.user_id=? ORDER BY s.created_at DESC",userId);
 }
 public boolean respond(int userId,int id,String status)throws SQLException {
  try(Connection c=DBConnection.getConnection()){return JobDAO.update(c,"UPDATE solicitudes_contacto s JOIN egresados g ON g.graduate_id=s.graduate_id SET s.status=? WHERE s.request_id=? AND g.user_id=?",status,id,userId)==1;}
 }
 public boolean contactAccepted(int graduate,int companyUser)throws SQLException {
  return !JobDAO.rows("SELECT 1 FROM solicitudes_contacto s JOIN empresas e ON e.company_id=s.company_id WHERE s.graduate_id=? AND e.user_id=? AND s.status='accepted' LIMIT 1",graduate,companyUser).isEmpty();
 }
 public Integer graduateForCv(int cvId)throws SQLException {
  var rows=JobDAO.rows("SELECT graduate_id FROM cv WHERE cv_id=?",cvId);
  return rows.isEmpty()?null:((Number)rows.get(0).get("graduate_id")).intValue();
 }
 public String authorizedFile(int cv,int user,String role,String type,boolean applicationAccess)throws SQLException {
  var rows=JobDAO.rows("SELECT g.photo_url,cv.cv_pdf_url FROM cv JOIN egresados g ON g.graduate_id=cv.graduate_id JOIN usuarios u ON u.user_id=g.user_id WHERE cv.cv_id=? AND (g.user_id=? OR (?='company' AND u.role='graduate' AND u.status='active' AND g.is_public=1 AND cv.is_published=1 AND (?='photo' OR EXISTS(SELECT 1 FROM solicitudes_contacto s JOIN empresas e ON e.company_id=s.company_id WHERE s.graduate_id=g.graduate_id AND e.user_id=? AND s.status='accepted'))) OR ?=1)",cv,user,role,type,user,applicationAccess?1:0);
  return rows.isEmpty()?null:(String)rows.get(0).get(type.equals("photo")?"photo_url":"cv_pdf_url");
 }
 public String replaceFile(int userId,String type,String stored)throws SQLException {
  try(Connection c=DBConnection.getConnection()){c.setAutoCommit(false);try{
   var rows=JobDAO.rows(c,"SELECT g.photo_url,cv.cv_pdf_url FROM egresados g JOIN cv ON cv.graduate_id=g.graduate_id WHERE g.user_id=? FOR UPDATE",userId);
   if(rows.isEmpty())throw new IllegalArgumentException("Perfil no encontrado.");
   String old=(String)rows.get(0).get(type.equals("photo")?"photo_url":"cv_pdf_url");
   String sql=type.equals("photo")?"UPDATE egresados SET photo_url=? WHERE user_id=?":"UPDATE cv JOIN egresados g ON g.graduate_id=cv.graduate_id SET cv.cv_pdf_url=? WHERE g.user_id=?";
   JobDAO.update(c,sql,stored,userId);c.commit();return old;
  }catch(SQLException|RuntimeException e){c.rollback();throw e;}}
 }
}
