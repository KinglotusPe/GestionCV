package com.cvmanager.dao.interfaces;
import java.sql.*;
import java.util.*;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface ProfileDAO {
    Map<String,Object> company(int userId)throws SQLException;
    void saveCompany(int userId,Map<String,String> values)throws SQLException;
    List<Map<String,Object>> requests(int userId)throws SQLException;
    boolean respond(int userId,int id,String status)throws SQLException;
    boolean contactAccepted(int graduate,int companyUser)throws SQLException;
    Integer graduateForCv(int cvId)throws SQLException;
    String authorizedFile(int cv,int user,String role,String type,boolean applicationAccess)throws SQLException;
    String replaceFile(int userId,String type,String stored)throws SQLException;
}
