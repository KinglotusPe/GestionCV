package pe.edu.hierropozo.cvmanager.util;
/** Punto común de auditoría; la persistencia reside en AuditDAO. */
public final class AuditUtil {
 private AuditUtil(){}
 public static void log(Integer userId,String action,String entityType,Integer entityId,String details,String ipAddress){pe.edu.hierropozo.cvmanager.dao.AuditDAO.log(userId,action,entityType,entityId,details,ipAddress);}
}
