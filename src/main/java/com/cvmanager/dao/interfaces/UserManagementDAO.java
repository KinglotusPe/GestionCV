package com.cvmanager.dao.interfaces;
import java.sql.*;
import java.util.*;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface UserManagementDAO {
    void update(int actor,int id,String role,String status,String first,String last,String company) throws Exception;
}
