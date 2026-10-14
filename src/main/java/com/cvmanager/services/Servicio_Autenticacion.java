package com.cvmanager.services;
import com.cvmanager.dao.interfaces.UsuarioDAO;
import com.cvmanager.dao.impl.UsuarioDAOImpl;
import com.cvmanager.models.User;
import com.cvmanager.utils.PasswordUtil;
/** Verifica credenciales; el controlador administra la sesión HTTP. */
public class Servicio_Autenticacion {
 private final UsuarioDAO usuarios=new UsuarioDAOImpl();
 public User authenticate(String email,String password) throws Exception {
  User user=usuarios.findByEmail(email);
  return user!=null && PasswordUtil.verify(password,user.getPasswordHash()) ? user : null;
 }
 public void recordLogin(int userId)throws Exception {usuarios.touchLogin(userId);}
}
