package com.cvmanager.models;
import java.util.*;
/** Datos tipados de la sección certification; admite fechas opcionales nulas. */
public record Certificacion(String name, String organization, java.sql.Date issueDate) {
 public List<Object> values() { return Arrays.asList(name, organization, issueDate); }
 public static Certificacion fromValidated(List<Object> values) {return new Certificacion((String) values.get(0), (String) values.get(1), (java.sql.Date) values.get(2));}
}
