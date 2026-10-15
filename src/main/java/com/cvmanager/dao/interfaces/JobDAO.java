package com.cvmanager.dao.interfaces;
import java.sql.*;
import java.util.*;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface JobDAO {
    List<Map<String,Object>> list(int actor,String role,String query,String stage,String archive) throws SQLException;
    Map<String,Object> find(int actor,String role,int id) throws SQLException;
    void save(int actor,String role,Integer id,Map<String,String> p,List<Integer> careers) throws SQLException;
    void updateStatus(int actor,String role,int id,String status) throws SQLException;
}
