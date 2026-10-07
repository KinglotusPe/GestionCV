package pe.edu.hierropozo.cvmanager.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestor centralizado de conexiones JDBC hacia MySQL.
 * Carga los parámetros de configuración desde database.properties
 * ubicado en el classpath (src/main/resources).
 */
public final class DBConnection {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IllegalStateException("El archivo 'database.properties' no fue encontrado en el classpath.");
            }
            properties.load(input);
            Class.forName(properties.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Error al inicializar el driver o cargar database.properties: " + e.getMessage());
        }
    }

    private DBConnection() {
        // Constructor privado para clase utilitaria
    }

    /**
     * Obtiene una nueva conexión a la base de datos MySQL.
     * @return Objeto Connection activo
     * @throws SQLException si ocurre un error de conexión
     */
    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}
