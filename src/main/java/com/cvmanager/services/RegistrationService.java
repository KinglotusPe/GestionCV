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

  public void registerGraduate(String email, String password, com.cvmanager.models.Egresados graduate) throws Exception {
    credentials(email, password);
    ValidacionUtil.text(graduate.getFirstName(), "Nombres", 80, true);
    ValidacionUtil.text(graduate.getLastName(), "Apellidos", 100, true);
    if (graduate.getDocumentNumber() != null && !graduate.getDocumentNumber().isBlank()) {
        if (!ValidacionUtil.dni(graduate.getDocumentNumber().trim())) {
            throw new IllegalArgumentException("DNI debe contener exactamente 8 dígitos numéricos.");
        }
        graduate.setDocumentNumber(graduate.getDocumentNumber().trim());
    }
    if (graduate.getPhone() != null && !graduate.getPhone().isBlank()) {
        if (!ValidacionUtil.phone(graduate.getPhone().trim())) {
            throw new IllegalArgumentException("Número de celular inválido.");
        }
        graduate.setPhone(graduate.getPhone().trim());
    }
    String status = graduate.getAcademicStatus();
    if (status == null || (!status.equals("ESTUDIANTE") && !status.equals("EGRESADO"))) {
        graduate.setAcademicStatus("EGRESADO");
    }
    dao.registerGraduate(email.trim(), PasswordUtil.hash(password), graduate);
  }

  public void registerCompany(String email, String password, java.util.Map<String, String> data) throws Exception {
    credentials(email, password);
    String companyName = ValidacionUtil.text(data.get("companyName"), "Razón Social", 150, true);
    data.put("companyName", companyName);
    String ruc = data.get("ruc");
    if (ruc != null && !ruc.isBlank()) {
        if (!ValidacionUtil.ruc(ruc.trim())) {
            throw new IllegalArgumentException("RUC debe contener exactamente 11 dígitos numéricos válidos.");
        }
        data.put("ruc", ruc.trim());
    }
    String phone = data.get("phone");
    if (phone != null && !phone.isBlank() && !ValidacionUtil.phone(phone.trim())) {
        throw new IllegalArgumentException("Teléfono de empresa inválido.");
    }
    dao.registerCompany(email.trim(), PasswordUtil.hash(password), data);
  }

  public void createAdmin(String email,String password)throws Exception{credentials(email,password);dao.createAdmin(email.trim(),PasswordUtil.hash(password));}
}
