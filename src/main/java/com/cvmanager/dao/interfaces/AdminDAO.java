package com.cvmanager.dao.interfaces;
import java.util.List;
import java.util.Map;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface AdminDAO {
    Map<String, Integer> stats() throws Exception;
    List<Map<String, Object>> byCareer() throws Exception;
    List<Map<String, Object>> getAuditLogs(int limit) throws Exception;
}
