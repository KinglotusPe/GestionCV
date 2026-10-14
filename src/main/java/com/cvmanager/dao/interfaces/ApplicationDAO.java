package com.cvmanager.dao.interfaces;
import java.sql.*;
import java.util.*;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface ApplicationDAO {
    List<Map<String,Object>> applications(int userId,boolean company)throws SQLException;
    void apply(int userId,int jobId,String message)throws SQLException;
    void respond(int userId,boolean company,int id,String status)throws SQLException;
    boolean canReadCv(int companyUserId,int cvId)throws SQLException;
}
