package pe.edu.hierropozo.cvmanager.config;
import java.io.*;
import java.sql.*;
import java.util.Properties;
import com.zaxxer.hikari.*;

/** Configurable connection pool. System properties are useful for isolated integration tests. */
public final class DBConnection {
    private static final HikariDataSource pool=createPool();
    private DBConnection() {}
    private static HikariDataSource createPool() {
        Properties p=new Properties();
        try(InputStream in=DBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if(in==null)throw new IllegalStateException("Falta database.properties");p.load(in);
        }catch(IOException e){throw new ExceptionInInitializerError(e);}
        HikariConfig c=new HikariConfig();
        c.setJdbcUrl(setting(p,"db.url","CVMANAGER_DB_URL"));
        c.setUsername(setting(p,"db.user","CVMANAGER_DB_USER"));
        c.setPassword(setting(p,"db.password","CVMANAGER_DB_PASSWORD"));
        c.setDriverClassName(p.getProperty("db.driver"));c.setMaximumPoolSize(10);c.setMinimumIdle(1);
        c.setConnectionTimeout(5000);c.setInitializationFailTimeout(-1);c.setPoolName("CVManager");
        return new HikariDataSource(c);
    }
    private static String setting(Properties p,String key,String env) {return System.getProperty(key,System.getenv().getOrDefault(env,p.getProperty(key,"")));}
    public static Connection getConnection()throws SQLException{return pool.getConnection();}
    public static void close(){pool.close();}
}
