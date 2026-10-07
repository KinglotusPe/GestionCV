package pe.edu.hierropozo.cvmanager.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import pe.edu.hierropozo.cvmanager.config.DBConnection;

/**
 * Listener del ciclo de vida del contexto de la aplicación web (ServletContextListener).
 * Valida la conectividad hacia MySQL durante el arranque del despliegue en Tomcat 11.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().log("==================================================");
        event.getServletContext().log("CVManager Hierro Pozo: Inicializando aplicación...");

        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                event.getServletContext().log("CVManager: Conexión con MySQL 8 verificada exitosamente.");
            }
        } catch (Exception e) {
            event.getServletContext().log("CVManager: ERROR al conectar con MySQL durante el inicio.", e);
        }

        event.getServletContext().log("CVManager: Contexto listo en Tomcat 11.");
        event.getServletContext().log("==================================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        event.getServletContext().log("CVManager Hierro Pozo: Deteniendo contexto de la aplicación.");
    }
}
