package com.cvmanager.services;
import java.util.*;
import com.cvmanager.dao.interfaces.UserManagementDAO;
import com.cvmanager.dao.impl.UserManagementDAOImpl;
public class UserManagementService {
 public void update(int actor,int id,String role,String status,String first,String last,String company)throws Exception {
        if(!Set.of("graduate","company","admin").contains(Objects.toString(role,"")) || !Set.of("active","inactive","pending","suspended").contains(Objects.toString(status,""))) throw new IllegalArgumentException("Rol o estado inválido.");
  new UserManagementDAOImpl().update(actor,id,role,status,first,last,company);
 }
}
