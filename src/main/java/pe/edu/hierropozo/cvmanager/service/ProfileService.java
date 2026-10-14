package pe.edu.hierropozo.cvmanager.service;
import java.util.*;
import pe.edu.hierropozo.cvmanager.dao.ProfileDAO;
import pe.edu.hierropozo.cvmanager.util.*;

/** Validación de perfiles y coordinación de archivos fuera del directorio público. */
public class ProfileService {
 private final ProfileDAO dao=new ProfileDAO();
 public Map<String,Object> company(int user)throws Exception{return dao.company(user);}
 public void saveCompany(int user,Map<String,String> input)throws Exception {
  var p=new HashMap<>(input);p.put("companyName",ValidationUtil.text(p.get("companyName"),"Empresa",150,true));
  String ruc=ValidationUtil.nullable(p.get("ruc")),phone=ValidationUtil.nullable(p.get("phone"));
  if(ruc!=null&&!ValidationUtil.ruc(ruc))throw new IllegalArgumentException("RUC inválido.");
  if(phone!=null&&!ValidationUtil.phone(phone))throw new IllegalArgumentException("Teléfono inválido.");
  p.put("ruc",ruc);p.put("phone",phone);p.put("industry",ValidationUtil.text(p.get("industry"),"Sector",100,false));
  p.put("companySize",ValidationUtil.text(p.get("companySize"),"Tamaño",50,false));p.put("website",ValidationUtil.url(p.get("website")));p.put("description",ValidationUtil.text(p.get("description"),"Descripción",5000,false));dao.saveCompany(user,p);
 }
 public List<Map<String,Object>> requests(int user)throws Exception{return dao.requests(user);}
 public boolean respond(int user,int id,String status)throws Exception {
  if(!Set.of("accepted","rejected").contains(Objects.toString(status,"")))throw new IllegalArgumentException("Respuesta inválida.");return dao.respond(user,id,status);
 }
 public boolean contactAccepted(int graduate,int companyUser)throws Exception{return dao.contactAccepted(graduate,companyUser);}
 public Integer graduateForCv(int cv)throws Exception{return dao.graduateForCv(cv);}
 public String file(int cv,int user,String role,String type)throws Exception {
  boolean access=role.equals("company")&&new JobService().canReadCv(user,cv);
  return dao.authorizedFile(cv,user,role,type,access);
 }
 public void replaceFile(int user,String type,byte[] bytes)throws Exception {
  if(!Set.of("photo","pdf").contains(Objects.toString(type,"")))throw new IllegalArgumentException("Tipo de archivo inválido.");
  String stored=bytes==null?null:FileStorage.save(type,bytes);boolean committed=false;
  try{String old=dao.replaceFile(user,type,stored);committed=true;FileStorage.delete(type,old);}finally{if(!committed)FileStorage.delete(type,stored);}
 }
}
