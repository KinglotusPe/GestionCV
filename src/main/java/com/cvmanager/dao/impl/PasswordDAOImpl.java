package com.cvmanager.dao.impl;
import com.cvmanager.dao.interfaces.*;
import java.sql.*;
import com.cvmanager.utils.DBConnection;

/** Tokens y cambios de credenciales atómicos; no recibe contraseñas en texto. */
public class PasswordDAOImpl implements PasswordDAO {
 public void issue(int userId,String tokenHash)throws SQLException {
  try(Connection c=DBConnection.getConnection()){c.setAutoCommit(false);try{
   lock(c,userId);
   JobDAOImpl.update(c,"DELETE FROM password_resets WHERE user_id=?",userId);
   JobDAOImpl.update(c,"INSERT INTO password_resets(token_hash,user_id,expires_at) VALUES(?,?,DATE_ADD(NOW(),INTERVAL 30 MINUTE))",tokenHash,userId);c.commit();
  }catch(SQLException|RuntimeException e){c.rollback();throw e;}}
 }
 public void change(int id,String expectedHash,String newHash)throws SQLException {
  try(Connection c=DBConnection.getConnection()){c.setAutoCommit(false);try{
   lock(c,id);
   if(JobDAOImpl.update(c,"UPDATE usuarios SET password_hash=? WHERE user_id=? AND password_hash=?",newHash,id,expectedHash)!=1)throw new IllegalArgumentException("La contraseña cambió. Inicie sesión nuevamente.");
   JobDAOImpl.update(c,"DELETE FROM password_resets WHERE user_id=?",id);c.commit();
  }catch(SQLException|RuntimeException e){c.rollback();throw e;}}
 }
 public void reset(String tokenHash,String newHash)throws SQLException {
  try(Connection c=DBConnection.getConnection()){c.setAutoCommit(false);try{
   var rows=JobDAOImpl.rows(c,"SELECT user_id FROM password_resets WHERE token_hash=? AND expires_at>NOW()",tokenHash);
   if(rows.isEmpty())throw new IllegalArgumentException("Enlace expirado o ya utilizado.");
   int id=((Number)rows.get(0).get("user_id")).intValue();lock(c,id);
   if(JobDAOImpl.update(c,"DELETE FROM password_resets WHERE token_hash=? AND expires_at>NOW()",tokenHash)!=1)throw new IllegalArgumentException("Enlace expirado o ya utilizado.");
   JobDAOImpl.update(c,"UPDATE usuarios SET password_hash=? WHERE user_id=?",newHash,id);
   JobDAOImpl.update(c,"DELETE FROM password_resets WHERE user_id=?",id);c.commit();
  }catch(SQLException|RuntimeException e){c.rollback();throw e;}}
 }
 private void lock(Connection c,int id)throws SQLException {
  if(JobDAOImpl.rows(c,"SELECT user_id FROM usuarios WHERE user_id=? FOR UPDATE",id).isEmpty())throw new IllegalArgumentException("Usuario inexistente.");
 }
}
