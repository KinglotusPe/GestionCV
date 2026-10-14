package com.cvmanager.services;
import java.util.*;
import com.cvmanager.utils.*;
import com.cvmanager.dao.interfaces.ProfileDAO;
import com.cvmanager.dao.impl.ProfileDAOImpl;
/** Autoriza descargas y coordina archivos con persistencia transaccional. */
public class Archivo_Servicio {
private final ProfileDAO dao=new ProfileDAOImpl();
 public String file(int cv,int user,String role,String type)throws Exception {
  boolean access=role.equals("company")&&new JobService().canReadCv(user,cv);
  return dao.authorizedFile(cv,user,role,type,access);
 }
 public void replaceFile(int user,String type,byte[] bytes)throws Exception {
  if(!Set.of("photo","pdf").contains(Objects.toString(type,"")))throw new IllegalArgumentException("Tipo de archivo inválido.");
  String stored=bytes==null?null:ArchivoUtil.save(type,bytes);boolean committed=false;
  try{String old=dao.replaceFile(user,type,stored);committed=true;ArchivoUtil.delete(type,old);}finally{if(!committed)ArchivoUtil.delete(type,stored);}
 }
}
