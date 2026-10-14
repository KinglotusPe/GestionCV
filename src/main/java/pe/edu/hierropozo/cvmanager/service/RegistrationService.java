package pe.edu.hierropozo.cvmanager.service;
import java.util.Set;
import pe.edu.hierropozo.cvmanager.dao.RegistrationDAO;
import pe.edu.hierropozo.cvmanager.util.*;
public class RegistrationService {
 private final RegistrationDAO dao=new RegistrationDAO();
 private void credentials(String email,String password){if(!ValidationUtil.email(email)||!ValidationUtil.password(password))throw new IllegalArgumentException("Correo o contraseña inválidos.");}
 public void register(String email,String password,String role,String firstName,String lastName,String companyName)throws Exception {
  credentials(email,password);
  if(role==null||!Set.of("graduate","company").contains(role))throw new IllegalArgumentException("Rol inválido.");
  if(role.equals("graduate")){firstName=ValidationUtil.text(firstName,"Nombres",80,true);lastName=ValidationUtil.text(lastName,"Apellidos",100,true);}
  else companyName=ValidationUtil.text(companyName,"Empresa",150,true);
  dao.register(email.trim(),PasswordUtil.hash(password),role,firstName,lastName,companyName);
 }
 public void createAdmin(String email,String password)throws Exception{credentials(email,password);dao.createAdmin(email.trim(),PasswordUtil.hash(password));}
}
