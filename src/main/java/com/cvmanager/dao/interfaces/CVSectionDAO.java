package com.cvmanager.dao.interfaces;
import java.sql.*;
import java.util.*;
import com.cvmanager.models.CVSectionDefinition.*;
/** Contrato de persistencia; la implementación JDBC reside en dao.impl. */
public interface CVSectionDAO {
    void save(int cvId,String type,Integer id,List<Object> validated)throws Exception;
}
