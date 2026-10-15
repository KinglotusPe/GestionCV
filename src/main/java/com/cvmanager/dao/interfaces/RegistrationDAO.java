package com.cvmanager.dao.interfaces;

import com.cvmanager.models.Egresados;
import java.util.Map;

/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface RegistrationDAO {
    void register(String email, String passwordHash, String role, String firstName, String lastName, String companyName) throws Exception;
    void registerGraduate(String email, String passwordHash, Egresados graduate) throws Exception;
    void registerCompany(String email, String passwordHash, Map<String, String> companyData) throws Exception;
    void createAdmin(String email, String passwordHash) throws Exception;
}
