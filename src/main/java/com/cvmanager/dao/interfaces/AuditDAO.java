package com.cvmanager.dao.interfaces;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface AuditDAO {
    void log(Integer userId, String action, String entityType, Integer entityId, String details, String ipAddress);
}
