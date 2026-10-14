package com.cvmanager.config;
import java.nio.file.Path;
import java.util.ResourceBundle;
/** Configuración común sin secretos en el repositorio. */
public final class AppConfig {
 private AppConfig() {}
 public static String message(String key) { return ResourceBundle.getBundle("messages").getString(key); }
 public static Path uploadDirectory() {
  String configured=System.getenv("CVMANAGER_UPLOAD_DIR");
  return Path.of(configured==null?System.getProperty("user.home")+"/cvmanager_uploads":configured).toAbsolutePath().normalize();
 }
}
