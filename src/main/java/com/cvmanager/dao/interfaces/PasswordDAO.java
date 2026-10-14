package com.cvmanager.dao.interfaces;
import java.sql.*;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface PasswordDAO {
    void issue(int userId,String tokenHash)throws SQLException;
    void change(int id,String expectedHash,String newHash)throws SQLException;
    void reset(String tokenHash,String newHash)throws SQLException;
}
