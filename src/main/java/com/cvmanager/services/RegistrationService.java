package com.cvmanager.services;
import java.util.Set;
import com.cvmanager.dao.interfaces.RegistrationDAO;
import com.cvmanager.dao.impl.RegistrationDAOImpl;
import com.cvmanager.utils.*;
public class RegistrationService {
 private final RegistrationDAO dao=new RegistrationDAOImpl();
 private void credentials(String email,String password){if(!ValidacionUtil.email(email)||!ValidacionUtil.password(password))throw new IllegalArgumentException("Correo o contraseña inválidos.");}
 public void register(String email,String password,String role,String firstName,String lastName,String companyName)throws Exception {
  credentials(email,password);
  if(role==null||!Set.of("graduate","company").contains(role))throw new IllegalArgumentException("Rol inválido.");
  if(role.equals("graduate")){firstName=ValidacionUtil.text(firstName,"Nombres",80,true);lastName=ValidacionUtil.text(lastName,"Apellidos",100,true);}
  else companyName=ValidacionUtil.text(companyName,"Empresa",150,true);
  dao.register(email.trim(),PasswordUtil.hash(password),role,firstName,lastName,companyName);
 }
 public void createAdmin(String email,String password)throws Exception{credentials(email,password);dao.createAdmin(email.trim(),PasswordUtil.hash(password));}
}
