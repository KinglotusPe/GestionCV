package com.cvmanager.dao.interfaces;
import java.sql.*;
import java.util.*;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface LaborPortalDAO {
    void archive(int actor,String role,int job,boolean archive)throws SQLException;
    List<Map<String,Object>> documents(int actor,String role,int job)throws SQLException;
    void addDocument(int actor,String role,int job,String title,String stored)throws SQLException;
    String download(int actor,String role,int document)throws SQLException;
    String removeDocument(int actor,String role,int job,int document)throws Exception;
    List<Map<String,Object>> companies()throws SQLException;
    List<Map<String,Object>> reviews()throws SQLException;
    com.cvmanager.models.CompanyReview review(int actor,int company,String decision,String reason)throws SQLException;
    List<Map<String,Object>> report(String kind,String start,String end)throws SQLException;
}
