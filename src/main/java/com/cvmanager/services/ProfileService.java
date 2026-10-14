package com.cvmanager.services;
import java.util.*;
import com.cvmanager.dao.interfaces.ProfileDAO;
import com.cvmanager.dao.impl.ProfileDAOImpl;
import com.cvmanager.utils.*;

/** Validación de perfiles y coordinación de archivos fuera del directorio público. */
public class ProfileService {
 private final ProfileDAO dao=new ProfileDAOImpl();
 public Map<String,Object> company(int user)throws Exception{return dao.company(user);}
 public void saveCompany(int user,Map<String,String> input)throws Exception {
  var p=new HashMap<>(input);p.put("companyName",ValidacionUtil.text(p.get("companyName"),"Empresa",150,true));
  String ruc=ValidacionUtil.nullable(p.get("ruc")),phone=ValidacionUtil.nullable(p.get("phone"));
  if(ruc!=null&&!ValidacionUtil.ruc(ruc))throw new IllegalArgumentException("RUC inválido.");
  if(phone!=null&&!ValidacionUtil.phone(phone))throw new IllegalArgumentException("Teléfono inválido.");
  p.put("ruc",ruc);p.put("phone",phone);p.put("industry",ValidacionUtil.text(p.get("industry"),"Sector",100,false));
  p.put("companySize",ValidacionUtil.text(p.get("companySize"),"Tamaño",50,false));p.put("website",ValidacionUtil.url(p.get("website")));p.put("description",ValidacionUtil.text(p.get("description"),"Descripción",5000,false));dao.saveCompany(user,p);
 }
 public List<Map<String,Object>> requests(int user)throws Exception{return dao.requests(user);}
 public boolean respond(int user,int id,String status)throws Exception {
  if(!Set.of("accepted","rejected").contains(Objects.toString(status,"")))throw new IllegalArgumentException("Respuesta inválida.");return dao.respond(user,id,status);
 }
 public boolean contactAccepted(int graduate,int companyUser)throws Exception{return dao.contactAccepted(graduate,companyUser);}
 public Integer graduateForCv(int cv)throws Exception{return dao.graduateForCv(cv);}
}
