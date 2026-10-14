package com.cvmanager.dao.interfaces;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface RegistrationDAO {
    void register(String email, String passwordHash, String role, String firstName, String lastName, String companyName) throws Exception;
    void createAdmin(String email, String passwordHash) throws Exception;
}
