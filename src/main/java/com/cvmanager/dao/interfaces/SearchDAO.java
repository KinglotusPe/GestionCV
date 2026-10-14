package com.cvmanager.dao.interfaces;
import java.util.List;
import java.util.Map;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface SearchDAO {
    List<Map<String, Object>> search(String query, String careerId, String skill, String city, String year, int page, int pageSize) throws Exception;
    void favorite(int userId, int cvId) throws Exception;
    List<Map<String, Object>> getFavorites(int userId) throws Exception;
    void removeFavorite(int userId, int favoriteId) throws Exception;
    void requestContact(int userId, int graduateId, String message) throws Exception;
    List<Map<String, Object>> getContactRequests(int userId) throws Exception;
    void recordView(int userId, int cvId) throws Exception;
}
