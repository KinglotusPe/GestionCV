package com.cvmanager.utils;
import java.io.*;
import java.sql.*;
import java.util.Properties;
import com.zaxxer.hikari.*;

/** Configurable connection pool. System properties are useful for isolated integration tests. */
public final class DBConnection {
    private static final HikariDataSource pool=com.cvmanager.config.DatabaseConfig.createPool();
    private DBConnection() {}
    public static Connection getConnection()throws SQLException{return pool.getConnection();}
    public static void close(){pool.close();}
}
