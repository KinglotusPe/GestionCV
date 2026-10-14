package com.cvmanager.models;
import java.util.*;
/** Datos tipados de la sección experience; admite fechas opcionales nulas. */
public record Experiencia(String companyName, String position, java.sql.Date startDate, java.sql.Date endDate, String responsibilities) {
 public List<Object> values() { return Arrays.asList(companyName, position, startDate, endDate, responsibilities); }
 public static Experiencia fromValidated(List<Object> values) {return new Experiencia((String) values.get(0), (String) values.get(1), (java.sql.Date) values.get(2), (java.sql.Date) values.get(3), (String) values.get(4));}
}
