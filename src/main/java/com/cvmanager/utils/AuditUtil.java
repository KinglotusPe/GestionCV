package com.cvmanager.utils;
/** Punto común de auditoría; la persistencia reside en AuditDAO. */
public final class AuditUtil {
 private AuditUtil(){}
 public static void log(Integer userId,String action,String entityType,Integer entityId,String details,String ipAddress){new com.cvmanager.dao.impl.AuditDAOImpl().log(userId,action,entityType,entityId,details,ipAddress);}
}
